package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.VendaObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final List<VendaObserver> observers;

    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService) {
        this.vendaRepository = Objects.requireNonNull(vendaRepository, "VendaRepository não pode ser nulo.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService não pode ser nulo.");
        this.observers = new ArrayList<>();
    }

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
                    System.err.println("Erro ao notificar observador de venda: " + e.getMessage());
                }
            }
        });
    }

    public Venda finalizarVenda(List<ItemCarrinho> carrinho, Long clienteId, String clienteNome) {
        if (carrinho == null || carrinho.isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio. Adicione ao menos um item antes de finalizar.");
        }

        for (ItemCarrinho itemCarrinho : carrinho) {
            Produto produto = produtoService.buscarPorId(itemCarrinho.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado: ID #" + itemCarrinho.getProdutoId()));

            Integer estoqueAtual = produto.getEstoque() != null ? produto.getEstoque() : 0;
            if (estoqueAtual < itemCarrinho.getQuantidade()) {
                throw new IllegalArgumentException(
                        String.format("Estoque insuficiente para o produto '%s'. Disponível: %d, solicitado: %d.",
                                produto.getNome(), estoqueAtual, itemCarrinho.getQuantidade()));
            }
        }

        Venda venda = new Venda(clienteId, clienteNome);

        for (ItemCarrinho itemCarrinho : carrinho) {
            Produto produto = produtoService.buscarPorId(itemCarrinho.getProdutoId()).orElseThrow();
            ItemVenda itemVenda = new ItemVenda(
                    venda,
                    produto,
                    itemCarrinho.getQuantidade(),
                    produto.getPreco()
            );
            venda.adicionarItem(itemVenda);

            Integer novoEstoque = produto.getEstoque() - itemCarrinho.getQuantidade();
            produto.setEstoque(novoEstoque);
            produtoService.atualizar(produto);
        }

        Venda vendaSalva = vendaRepository.salvar(venda);
        notificarObservadores();
        return vendaSalva;
    }

    public Venda cancelarVenda(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da venda é obrigatório para cancelamento.");
        }
        Venda cancelada = vendaRepository.cancelarVenda(id);
        notificarObservadores();
        produtoService.notificarObservadores();
        return cancelada;
    }

    public List<Venda> listarTodos() {
        return vendaRepository.listarTodos();
    }

    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.buscarPorId(id);
    }
}
