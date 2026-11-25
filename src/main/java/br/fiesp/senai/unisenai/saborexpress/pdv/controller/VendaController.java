package br.fiesp.senai.unisenai.saborexpress.pdv.controller;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.ProdutoService;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.VendaService;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.CaixaView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.HistoricoVendasView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.MainFrame;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.VendaTableModel;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VendaController {

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat
            .getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final MainFrame mainFrame;
    private final CaixaView caixaView;
    private final HistoricoVendasView historicoView;
    private final VendaTableModel vendaTableModel;
    private final ProdutoService produtoService;
    private final VendaService vendaService;

    public VendaController(MainFrame mainFrame, CaixaView caixaView,
                           HistoricoVendasView historicoView,
                           ProdutoService produtoService, VendaService vendaService) {
        this.mainFrame = Objects.requireNonNull(mainFrame, "MainFrame não pode ser nulo.");
        this.caixaView = Objects.requireNonNull(caixaView, "CaixaView não pode ser nula.");
        this.historicoView = Objects.requireNonNull(historicoView, "HistoricoVendasView não pode ser nula.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService não pode ser nulo.");
        this.vendaService = Objects.requireNonNull(vendaService, "VendaService não pode ser nulo.");
        this.vendaTableModel = this.historicoView.getVendasTableModel();

        this.vendaService.addObserver(this.vendaTableModel);
        vincularEventos();
    }

    public void inicializar() {
        this.vendaService.notificarObservadores();
        caixaView.atualizarProdutos(produtoService.listarAtivos());
        this.mainFrame.mostrarCaixa();
    }

    private void vincularEventos() {
        caixaView.getBtnAdicionarCarrinho().addActionListener(e -> onAdicionarAoCarrinho());
        caixaView.getBtnRemoverCarrinho().addActionListener(e -> onRemoverDoCarrinho());
        caixaView.getBtnLimparCarrinho().addActionListener(e -> onLimparCarrinho());
        caixaView.getBtnFinalizar().addActionListener(e -> onFinalizarVenda());
        caixaView.getBtnCancelar().addActionListener(e -> onCancelarVenda());

        caixaView.getBtnHistorico().addActionListener(e -> mainFrame.mostrarHistorico());
        historicoView.getBtnVoltar().addActionListener(e -> mainFrame.mostrarCaixa());
        historicoView.getBtnAtualizar().addActionListener(e -> onAtualizarHistorico());
        historicoView.getBtnCancelarVenda().addActionListener(e -> onCancelarVendaSelecionada());
        historicoView.getTabelaVendas().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onVendaSelecionadaNoHistorico();
            }
        });
    }

    private void onAdicionarAoCarrinho() {
        Produto produtoSelecionado = caixaView.getProdutoSelecionado();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um produto na tabela de produtos disponíveis.", "Atenção - Seleção Necessária", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantidadeTexto = caixaView.getQuantidadeTexto();
        Integer quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser um número inteiro positivo.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Quantidade inválida. Informe um número inteiro positivo (ex.: 2).", "Validação de Dados", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal precoUnitario = produtoSelecionado.getPreco();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        ItemCarrinho itemCarrinho = new ItemCarrinho(produtoSelecionado.getId(), produtoSelecionado.getNome(), quantidade, precoUnitario);
        caixaView.adicionarItemCarrinho(itemCarrinho.getNomeProduto(), itemCarrinho.getQuantidade(), itemCarrinho.getPrecoUnitario(), itemCarrinho.getSubtotal());
        atualizarTotalParcial();
        JOptionPane.showMessageDialog(mainFrame, String.format("Item adicionado ao carrinho:\n%s x%d - Subtotal: %s", produtoSelecionado.getNome(), quantidade, MOEDA_FORMATTER.format(subtotal)), "Item Adicionado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onRemoverDoCarrinho() {
        int selectedRow = caixaView.getCarrinhoSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um item na tabela do carrinho para remover.", "Atenção - Seleção Necessária", JOptionPane.WARNING_MESSAGE);
            return;
        }
        caixaView.removerItemCarrinhoSelecionado();
        atualizarTotalParcial();
    }

    private void onLimparCarrinho() {
        int confirmacao = JOptionPane.showConfirmDialog(mainFrame, "Deseja realmente limpar todos os itens do carrinho?", "Confirmar Limpeza do Carrinho", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacao == JOptionPane.YES_OPTION) {
            caixaView.limparCarrinho();
            atualizarTotalParcial();
        }
    }

    private void onFinalizarVenda() {
        int rowCount = caixaView.getTabelaCarrinho().getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(mainFrame, "O carrinho está vazio. Adicione ao menos um item antes de finalizar a venda.", "Atenção - Carrinho Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<ItemCarrinho> carrinho = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String nomeProduto = (String) caixaView.getTabelaCarrinho().getValueAt(i, 0);
            Integer quantidade = (Integer) caixaView.getTabelaCarrinho().getValueAt(i, 1);
            Object precoObj = caixaView.getTabelaCarrinho().getValueAt(i, 2);
            BigDecimal precoUnitario = precoObj instanceof BigDecimal ? (BigDecimal) precoObj : new BigDecimal(precoObj.toString().replace("R$", "").replace(".", "").replace(",", ".").trim());

            Produto produto = produtoService.buscarPorNome(nomeProduto)
                    .stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Produto não encontrado: " + nomeProduto));
            carrinho.add(new ItemCarrinho(produto.getId(), nomeProduto, quantidade, precoUnitario));
        }

        String clienteNome = caixaView.getClienteNome();
        Long clienteId = null;
        try {
            Venda venda = vendaService.finalizarVenda(carrinho, clienteId, clienteNome);
            JOptionPane.showMessageDialog(mainFrame,
                    String.format("Venda finalizada com sucesso!\n\nVenda #%d\nTotal: %s\nCliente: %s",
                            venda.getId(),
                            MOEDA_FORMATTER.format(venda.getTotal()),
                            (venda.getClienteNome() != null && !venda.getClienteNome().isBlank()) ? venda.getClienteNome() : "(sem cliente)"),
                    "Venda Finalizada", JOptionPane.INFORMATION_MESSAGE);
            caixaView.limparCarrinho();
            atualizarTotalParcial();
            caixaView.getTxtCliente().setText("");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Validação de Venda", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Falha ao finalizar venda: " + ex.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelarVenda() {
        int confirmacao = JOptionPane.showConfirmDialog(mainFrame, "Deseja cancelar a venda atual e limpar o carrinho?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacao == JOptionPane.YES_OPTION) {
            caixaView.limparCarrinho();
            atualizarTotalParcial();
            caixaView.getTxtCliente().setText("");
            caixaView.getTxtQuantidade().setText("1");
        }
    }

    private void onAtualizarHistorico() {
        this.vendaService.notificarObservadores();
    }

    private void onCancelarVendaSelecionada() {
        Venda selecionada = historicoView.getVendaSelecionada();
        if (selecionada == null || selecionada.getId() == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione uma venda no histórico para cancelar.", "Atenção - Seleção Necessária", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Venda.STATUS_CANCELADA.equals(selecionada.getStatus())) {
            JOptionPane.showMessageDialog(mainFrame, "A venda #" + selecionada.getId() + " já está cancelada.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(mainFrame,
                "Deseja realmente cancelar a venda #" + selecionada.getId() + "?\nO estoque dos itens será estornado.",
                "Confirmar Cancelamento de Venda",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            vendaService.cancelarVenda(selecionada.getId());
            JOptionPane.showMessageDialog(mainFrame, "Venda #" + selecionada.getId() + " cancelada com sucesso. Estoque estornado.", "Venda Cancelada", JOptionPane.INFORMATION_MESSAGE);
            historicoView.exibirDetalhesVenda(null, java.util.Collections.emptyList());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Validação de Cancelamento", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Falha ao cancelar venda: " + ex.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onVendaSelecionadaNoHistorico() {
        int selectedRow = historicoView.getTabelaVendas().getSelectedRow();
        if (selectedRow < 0) {
            historicoView.exibirDetalhesVenda(null, java.util.Collections.emptyList());
            return;
        }

        Venda vendaResumo = historicoView.getVendasTableModel().getVendaAt(selectedRow);
        if (vendaResumo == null || vendaResumo.getId() == null) {
            historicoView.exibirDetalhesVenda(null, java.util.Collections.emptyList());
            return;
        }

        vendaService.buscarPorId(vendaResumo.getId()).ifPresentOrElse(vendaCompleta -> {
            List<br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda> itens = vendaCompleta.getItens();
            if (itens != null) {
                for (br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda item : itens) {
                    if (item.getProduto() != null && item.getProduto().getId() != null) {
                        produtoService.buscarPorId(item.getProduto().getId()).ifPresent(item::setProduto);
                    }
                }
            }
            historicoView.exibirDetalhesVenda(vendaCompleta.getId(), itens);
        }, () -> historicoView.exibirDetalhesVenda(null, java.util.Collections.emptyList()));
    }

    private void atualizarTotalParcial() {
        BigDecimal total = BigDecimal.ZERO;
        int rowCount = caixaView.getTabelaCarrinho().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object subtotalObj = caixaView.getTabelaCarrinho().getValueAt(i, 3);
            BigDecimal subtotal = subtotalObj instanceof BigDecimal ? (BigDecimal) subtotalObj : new BigDecimal(subtotalObj.toString().replace("R$", "").replace(".", "").replace(",", ".").trim());
            if (subtotal != null) {
                total = total.add(subtotal);
            }
        }
        caixaView.atualizarTotal(total);
    }

    public VendaTableModel getVendaTableModel() {
        return vendaTableModel;
    }
}
