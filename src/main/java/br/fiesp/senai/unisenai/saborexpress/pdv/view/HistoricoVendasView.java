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
import java.util.Collections;
import java.util.List;

public class HistoricoVendasView extends JPanel {

    private final JTable tabelaVendas;
    private final VendaTableModel vendasTableModel;
    private final JTable tabelaItens;
    private final ItensVendaTableModel itensTableModel;
    private final JButton btnAtualizar;
    private final JButton btnCancelarVenda;
    private final JButton btnVoltar;
    private final JLabel lblItens;

    public HistoricoVendasView(VendaTableModel vendasTableModel) {
        this.vendasTableModel = vendasTableModel;
        this.itensTableModel = new ItensVendaTableModel();
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));
        JLabel lblTitulo = new JLabel("Histórico de Vendas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblSubtitulo = new JLabel("Consulta de vendas finalizadas e detalhes dos itens de cada pedido.");
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
        painelVendas.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(" Vendas Finalizadas "), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tabelaVendas = new JTable(vendasTableModel);
        tabelaVendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaVendas.setRowHeight(28);
        tabelaVendas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaVendas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelVendas.add(new JScrollPane(tabelaVendas), BorderLayout.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        painelCentral.add(painelVendas, gbc);

        JPanel painelItens = new JPanel(new BorderLayout(10, 10));
        painelItens.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(" Detalhes da Venda Selecionada "), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        lblItens = new JLabel("Itens da venda:");
        lblItens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelItens.add(lblItens, BorderLayout.NORTH);
        tabelaItens = new JTable(itensTableModel);
        tabelaItens.setRowHeight(26);
        tabelaItens.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaItens.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        painelItens.add(new JScrollPane(tabelaItens), BorderLayout.CENTER);
        gbc.gridx = 1; gbc.gridy = 0;
        painelCentral.add(painelItens, gbc);

        add(painelCentral, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnVoltar = new JButton("Voltar ao Caixa");
        btnVoltar.setPreferredSize(new Dimension(130, 35));
        btnCancelarVenda = new JButton("Cancelar Venda");
        btnCancelarVenda.setPreferredSize(new Dimension(130, 35));
        btnCancelarVenda.setForeground(new Color(180, 40, 40));
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(110, 35));
        painelRodape.add(btnVoltar);
        painelRodape.add(btnCancelarVenda);
        painelRodape.add(btnAtualizar);
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
            itensTableModel.setItens(Collections.emptyList());
            lblItens.setText("Itens da venda:");
        }
    }

    public Venda getVendaSelecionada() {
        int selectedRow = tabelaVendas.getSelectedRow();
        if (selectedRow >= 0) return vendasTableModel.getVendaAt(selectedRow);
        return null;
    }
}
