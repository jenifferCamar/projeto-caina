package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.ItensVendaTableModel;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.VendaTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class HistoricoVendasView extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JLabel lblVendas;
    private final JTable tabelaVendas;
    private final VendaTableModel vendasTableModel;
    private final JLabel lblItens;
    private final JTable tabelaItens;
    private final ItensVendaTableModel itensTableModel;
    private final JButton btnAtualizar;
    private final JButton btnCancelarVenda;
    private final JButton btnVoltar;

    public HistoricoVendasView(VendaTableModel vendasTableModel) {
        this.vendasTableModel = vendasTableModel;
        this.itensTableModel = new ItensVendaTableModel();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));

        lblTitulo = new JLabel("Histórico de Vendas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblSubtitulo = new JLabel("Consulta de vendas finalizadas e detalhes dos itens de cada pedido.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);

        painelCabecalho.add(lblTitulo);
        painelCabecalho.add(Box.createVerticalStrut(4));
        painelCabecalho.add(lblSubtitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel painelVendas = new JPanel(new BorderLayout(10, 10));
        painelVendas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Vendas Finalizadas "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        lblVendas = new JLabel("Selecione uma venda para visualizar os detalhes:");
        lblVendas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelVendas.add(lblVendas, BorderLayout.NORTH);

        tabelaVendas = new JTable(vendasTableModel);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setRowHeight(28);
        tabelaVendas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaVendas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaVendas.getTableHeader().setReorderingAllowed(false);
        tabelaVendas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaVendas.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabelaVendas.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabelaVendas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaVendas.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollVendas = new JScrollPane(tabelaVendas);
        scrollVendas.setBorder(BorderFactory.createTitledBorder(" Lista de Vendas "));
        painelVendas.add(scrollVendas, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCentral.add(painelVendas, gbc);

        JPanel painelItens = new JPanel(new BorderLayout(10, 10));
        painelItens.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Detalhes da Venda Selecionada "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        lblItens = new JLabel("Itens da venda:");
        lblItens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelItens.add(lblItens, BorderLayout.NORTH);

        tabelaItens = new JTable(itensTableModel);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaItens.setRowHeight(26);
        tabelaItens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaItens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaItens.getTableHeader().setReorderingAllowed(false);
        tabelaItens.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabelaItens.getColumnModel().getColumn(1).setPreferredWidth(80);
        tabelaItens.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaItens.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setBorder(BorderFactory.createTitledBorder(" Itens da Venda "));
        painelItens.add(scrollItens, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 0;
        painelCentral.add(painelItens, gbc);

        add(painelCentral, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblStatus = new JLabel("Selecione uma venda na tabela para ver os detalhes.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        painelRodape.add(lblStatus, BorderLayout.WEST);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnVoltar = new JButton("Voltar ao Caixa");
        btnVoltar.setPreferredSize(new Dimension(130, 35));
        btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setPreferredSize(new Dimension(130, 35));
        btnCancelarVenda.setForeground(new Color(180, 40, 40));
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(110, 35));

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnCancelarVenda);
        painelBotoes.add(btnAtualizar);
        painelRodape.add(painelBotoes, BorderLayout.EAST);

        add(painelRodape, BorderLayout.SOUTH);
    }

    public VendaTableModel getVendasTableModel() { return vendasTableModel; }
    public JTable getTabelaVendas() { return tabelaVendas; }
    public JButton getBtnAtualizar() { return btnAtualizar; }
    public JButton getBtnCancelarVenda() { return btnCancelarVenda; }
    public JButton getBtnVoltar() { return btnVoltar; }

    public void exibirDetalhesVenda(Long vendaId, List<ItemVenda> itens) {
        if (vendaId != null && itens != null) {
            itensTableModel.setItens(itens);
            lblItens.setText("Itens da venda #" + vendaId + ":");
        } else {
            itensTableModel.setItens(java.util.Collections.emptyList());
            lblItens.setText("Itens da venda:");
        }
    }

    public ItensVendaTableModel getItensTableModel() { return itensTableModel; }

    public Venda getVendaSelecionada() {
        int selectedRow = tabelaVendas.getSelectedRow();
        if (selectedRow >= 0) {
            return vendasTableModel.getVendaAt(selectedRow);
        }
        return null;
    }
}
