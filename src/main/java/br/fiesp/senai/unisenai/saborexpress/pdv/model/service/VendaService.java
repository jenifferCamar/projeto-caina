package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.EstoqueInsuficienteException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.BusinessException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ValidationException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.VendaObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Camada de Serviço de Negócio para {@link Venda} (Service Layer / Subject no
 * Padrão Observer).
 * <p>
 * <b>Conceito Pedagógico (Regras de Negócio, IoC, Transações e Thread-Safety):</b>
 * <ul>
 * <li><b>Orquestrador de Venda:</b> Recebe o carrinho de itens temporários,
 * valida estoque, aplica baixas de estoque nos produtos, persiste a {@link Venda}
 * e seus {@link ItemVenda} de forma atômica via {@link VendaRepository}.</li>
 * <li><b>Validação de Estoque:</b> Antes de finalizar, verifica se cada produto
 * do carrinho possui quantidade suficiente em estoque, lançando
 * {@link BusinessException} ou {@link ValidationException} com mensagem amigável caso contrário.</li>
 * <li><b>Baixa de Estoque:</b> Decrementa o estoque de cada produto vendido e
 * persiste a alteração via {@link ProdutoService}, garantindo consistência
 * entre o cardápio e o histórico de vendas.</li>
 * <li><b>Transação Atômica:</b> A persistência da venda e seus itens é feita
 * dentro de uma única transação JPA (commit/rollback) no repositório.</li>
 * <li><b>Padrão Observer (Subject):</b> Mantém a lista de ouvintes registrados
 * e dispara notificações reativas após cada venda finalizada com sucesso.</li>
 * <li><b>Thread Safety com Swing (EDT):</b> Assegura que o método
 * {@link VendaObserver#onVendasChanged(List)} seja sempre despachado na
 * <i>Event Dispatch Thread</i> do Swing via
 * {@link SwingUtilities#invokeLater(Runnable)}.</li>
 * </ul>
 */
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final List<VendaObserver> observers;

    /**
     * Construtor com Injeção de Dependências dos repositórios e serviços.
     *
     * @param vendaRepository   Abstração do repositório de vendas.
     * @param produtoService    Camada de serviço de produtos (para validação e
     *                          baixa de estoque).
     */
    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository não pode ser nulo.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService não pode ser nulo.");
        this.observers = new ArrayList<>();
    }

    // ==========================================
    // GERENCIAMENTO DE OBSERVERS (GOF PATTERN)
    // ==========================================

    /**
     * Registra um novo observador para receber notificações de mudanças em vendas.
     *
     * @param observer Implementação de {@link VendaObserver}.
     */
    public synchronized void addObserver(VendaObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Remove um observador previamente registrado.
     *
     * @param observer Observador a ser removido.
     */
    public synchronized void removeObserver(VendaObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica todos os observadores cadastrados com a lista atualizada de vendas.
     * Garante o despacho seguro na Event Dispatch Thread (EDT) do Swing.
     */
    public void notificarObservadores() {
        List<Venda> listaAtualizada = vendaRepository.listarTodos();
        List<VendaObserver> copiaObservers;
        synchronized (this) {
            copiaObservers = new ArrayList<>(observers);
        }

        // Despacho seguro na Thread de Eventos do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            for (VendaObserver observer : copiaObservers) {
                try {
                    observer.onVendasChanged(Collections.unmodifiableList(listaAtualizada));
                } catch (Exception e) {
                    System.err.println("Erro ao notificar observador de venda: " + e.getMessage());
                }
            }
        });
    }

    // ==========================================
    // REGRAS DE NEGÓCIO - VENDAS
    // ==========================================

    /**
     * Finaliza uma venda a partir do carrinho de itens temporários.
     * <p>
     * Valida a disponibilidade de estoque de cada produto, aplica a baixa de
     * estoque, persiste a venda e seus itens de forma atômica e notifica os
     * observadores registrados.
     *
     * @param carrinho     Lista de itens temporários do carrinho de compras.
     * @param clienteId    ID opcional do cliente (pode ser {@code null} para
     *                     venda sem cliente).
     * @param clienteNome  Nome opcional do cliente para exibição no histórico.
     * @return A {@link Venda} finalizada e persistida.
     * @throws ValidationException se dados forem inválidos.
     * @throws BusinessException se houver regra de negócio violada.
     */
    public Venda finalizarVenda(List<ItemCarrinho> carrinho, Long clienteId, String clienteNome) {
        if (carrinho == null || carrinho.isEmpty()) {
            throw new ValidationException("O carrinho está vazio. Adicione ao menos um item antes de finalizar.");
        }

        // 1. Validação de estoque para cada item do carrinho
        for (ItemCarrinho itemCarrinho : carrinho) {
            Produto produto = produtoService.buscarPorId(itemCarrinho.getProdutoId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado: " + itemCarrinho.getProdutoId()));

            // Validação preemptiva de produto inativo
            produtoService.validarDisponibilidadeParaVenda(produto);

            Integer estoqueAtual = produto.getEstoque() != null ? produto.getEstoque() : 0;
            if (estoqueAtual < itemCarrinho.getQuantidade()) {
                throw new EstoqueInsuficienteException(produto.getNome(), estoqueAtual,
                        itemCarrinho.getQuantidade());
            }
        }

        // 2. Cria a entidade Venda
        Venda venda = new Venda(clienteId, clienteNome);

        // 3. Adiciona itens, aplica baixa de estoque e persiste cada produto
        for (ItemCarrinho itemCarrinho : carrinho) {
            Produto produto = produtoService.buscarPorId(itemCarrinho.getProdutoId()).orElseThrow();

            // Cria o ItemVenda associado à venda
            ItemVenda itemVenda = new ItemVenda(
                    venda,
                    produto,
                    itemCarrinho.getQuantidade(),
                    produto.getPreco()
            );
            venda.adicionarItem(itemVenda);

            // Aplica baixa de estoque
            Integer novoEstoque = produto.getEstoque() - itemCarrinho.getQuantidade();
            produto.setEstoque(novoEstoque);
            produtoService.atualizar(produto);
        }

        // 4. Persiste a venda (e itens via cascade) de forma atômica
        Venda vendaSalva = vendaRepository.salvar(venda);

        // 5. Notifica observadores de forma reativa (histórico, tabela, etc.)
        notificarObservadores();

        return vendaSalva;
    }

    /**
     * Retorna a listagem de todas as vendas registradas, ordenadas por data/hora
     * decrescente.
     *
     * @return Lista de vendas.
     */


    public Venda cancelarVenda(Long id) {
        if (id == null) {
            throw new ValidationException("ID da venda é obrigatório para cancelamento.");
        }
        Venda cancelada = vendaRepository.cancelarVenda(id);
        notificarObservadores();
        produtoService.notificarObservadores();
        return cancelada;
    }
    public List<Venda> listarTodos() {
        return vendaRepository.listarTodos();
    }

    /**
     * Busca uma venda pelo seu identificador, incluindo os itens associados.
     *
     * @param id Identificador da venda.
     * @return {@link Optional} contendo a venda, se encontrada.
     */
    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.buscarPorId(id);
    }
}
