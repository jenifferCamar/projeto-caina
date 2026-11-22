package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.VendaObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Camada de Servico de Negocio para Vendas.
 * <p>
 * <b>Conceito Pedagogico (Regras de Negocio, IoC e Thread-Safety):</b>
 * <ul>
 * <li><b>Validacao de Dominio:</b> Aplica validacoes estritas (venda com itens,
 * quantidades positivas, produto existente, estoque suficiente).</li>
 * <li><b>Inversao de Dependencia (DIP):</b> Depende das abstracoes
 * {@link VendaRepository} e {@link ProdutoRepository}.</li>
 * <li><b>Padrao Observer (Subject):</b> Mantem lista de ouvintes e dispara
 * notificacoes reativas apos mutacoes bem-sucedidas.</li>
 * <li><b>Thread Safety com Swing (EDT):</b> Notificacoes despachadas na Event
 * Dispatch Thread via SwingUtilities.invokeLater.</li>
 * </ul>
 * <p>
 * <b>Transacao atomica:</b> A persistencia da venda e a baixa de estoque ocorrem
 * em uma unica transacao no Repository (finalizarVenda), garantindo que nao haja
 * estado parcial em caso de falha (commit/rollback).
 */
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final List<VendaObserver> observers;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository nao pode ser nulo.");
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository nao pode ser nulo.");
        this.observers = new ArrayList<>();
    }

    // ==========================================
    // GERENCIAMENTO DE OBSERVERS
    // ==========================================

    public synchronized void addObserver(VendaObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(VendaObserver observer) {
        observers.remove(observer);
    }

    public void notificarObservadores() {
        List<Venda> listaAtualizada = vendaRepository.listarTodos();
        List<VendaObserver> copiaObservers;
        synchronized (this) {
            copiaObservers = new ArrayList<>(observers);
        }
        SwingUtilities.invokeLater(() -> {
            for (VendaObserver observer : copiaObservers) {
                try {
                    observer.onVendasChanged(Collections.unmodifiableList(listaAtualizada));
                } catch (Exception e) {
                    System.err.println("Erro ao notificar observador de vendas: " + e.getMessage());
                }
            }
        });
    }

    // ==========================================
    // REGRAS DE NEGOCIO E PERSISTENCIA
    // ==========================================

    /**
     * Finaliza uma venda: valida itens, verifica estoque, calcula total e
     * persiste de forma atomica (venda + itens + baixa de estoque).
     *
     * @param venda Venda contendo os itens (produto + quantidade).
     * @return Venda persistida com ID gerado.
     */
    public Venda finalizarVenda(Venda venda) {
        // 1. Validacao basica
        if (venda == null) {
            throw new IllegalArgumentException("Dados da venda nao podem ser nulos.");
        }
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter ao menos um item.");
        }

        validarFormaPagamento(venda.getFormaPagamento());
        validarTransicaoStatus(venda.getStatus(), Venda.STATUS_FINALIZADA);
        venda.setStatus(Venda.STATUS_FINALIZADA);

        // 2. Validacao de cada item: produto existente, quantidade positiva, estoque suficiente
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto() == null || item.getProduto().getId() == null) {
                throw new IllegalArgumentException("Produto do item nao pode ser nulo.");
            }
            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new IllegalArgumentException(
                        "Quantidade deve ser maior que zero para o produto: " +
                                (item.getProduto().getNome() != null ? item.getProduto().getNome() : item.getProduto().getId()));
            }

            // Busca o produto atualizado do banco para verificar estoque
            Optional<Produto> produtoOpt = produtoRepository.buscarPorId(item.getProduto().getId());
            if (produtoOpt.isEmpty()) {
                throw new IllegalArgumentException("Produto nao encontrado: " + item.getProduto().getId());
            }
            Produto produto = produtoOpt.get();
            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalStateException(
                        "Estoque insuficiente para '" + produto.getNome() +
                                "': disponivel=" + produto.getQuantidadeEstoque() +
                                ", solicitado=" + item.getQuantidade());
            }

            // Preenche preco unitario e subtotal a partir do produto (se nao informados)
            if (item.getPrecoUnitario() == null) {
                item.setPrecoUnitario(produto.getPreco());
            }
            item.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
        }

        // 3. Calcula o total
        venda.recalcularTotal();

        // 4. Persiste de forma atomica (venda + itens + baixa de estoque)
        Venda salva = vendaRepository.finalizarVenda(venda);

        // 5. Notifica observadores (reatividade de UI)
        notificarObservadores();

        return salva;
    }

    /**
     * Busca uma venda pelo ID.
     */
    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.buscarPorId(id);
    }

    /**
     * Retorna a listagem de todas as vendas.
     */
    public List<Venda> listarTodos() {
        return vendaRepository.listarTodos();
    }

    /**
     * Cancela uma venda e estorna o estoque (implementacao basica).
     * Refinado no Card 17.
     */
    public void cancelar(Long id) {
        Optional<Venda> vendaOpt = vendaRepository.buscarPorId(id);
        if (vendaOpt.isEmpty()) {
            throw new IllegalArgumentException("Venda nao encontrada: " + id);
        }
        Venda venda = vendaOpt.get();
        validarTransicaoStatus(venda.getStatus(), Venda.STATUS_CANCELADA);
        venda.setStatus(Venda.STATUS_CANCELADA);
        vendaRepository.atualizar(venda);
        notificarObservadores();
    }

    // ==========================================
    // VALIDACOES DE PAGAMENTO E STATUS (Card 16)
    // ==========================================

    private void validarFormaPagamento(String formaPagamento) {
        if (formaPagamento == null || formaPagamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Forma de pagamento e obrigatoria.");
        }
        String normalizada = formaPagamento.trim().toUpperCase();
        if (!normalizada.equals(Venda.PAGAMENTO_DINHEIRO)
                && !normalizada.equals(Venda.PAGAMENTO_CARTAO_DEBITO)
                && !normalizada.equals(Venda.PAGAMENTO_CARTAO_CREDITO)
                && !normalizada.equals(Venda.PAGAMENTO_PIX)) {
            throw new IllegalArgumentException("Forma de pagamento invalida: " + formaPagamento);
        }
    }

    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        String atual = (statusAtual == null || statusAtual.trim().isEmpty())
                ? Venda.STATUS_ABERTO : statusAtual.trim().toUpperCase();
        String destino = novoStatus == null ? "" : novoStatus.trim().toUpperCase();

        if (Venda.STATUS_FINALIZADA.equals(destino)) {
            if (!Venda.STATUS_ABERTO.equals(atual)) {
                throw new IllegalStateException(
                        "Apenas vendas em aberto podem ser finalizadas. Status atual: " + atual);
            }
            return;
        }
        if (Venda.STATUS_CANCELADA.equals(destino)) {
            if (!Venda.STATUS_FINALIZADA.equals(atual)) {
                throw new IllegalStateException(
                        "Apenas vendas finalizadas podem ser canceladas. Status atual: " + atual);
            }
            return;
        }
        throw new IllegalStateException("Transicao de status invalida: " + atual + " -> " + destino);
    }
}
