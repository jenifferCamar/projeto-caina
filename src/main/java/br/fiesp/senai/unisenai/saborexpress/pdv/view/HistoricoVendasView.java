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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Visão (View) do Histórico de Vendas do PDV.
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC - Separação da Camada View):</b>
 * <ul>
 * <li>Esta classe é estritamente responsável pela apresentação gráfica da tela
 * de histórico de vendas e pelos detalhes dos itens de cada venda selecionada.</li>
 * <li><b>Não contém:</b> Acesso a banco de dados (JPA/SQL), regras de negócio
 * ou lógica transacional. Toda a conversação com o modelo passa pelos
 * {@code TableModel}s e pelo {@code VendaController}.</li>
 * <li>Apresenta a listagem de vendas finalizadas em uma tabela principal e, ao
 * selecionar uma linha, exibe os detalhes (itens) da venda correspondente em uma
 * tabela secundária.</li>
 * <li>Recebe o {@link VendaTableModel} já populado pelo controlador, que atua
 * como observador do {@code VendaService} e é atualizado reativamente após cada
 * nova venda finalizada.</li>
 * </ul>
 */
public class HistoricoVendasView extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;

    // --- Tabela principal de Vendas ---
    private final JLabel lblVendas;
    private final JTable tabelaVendas;
    private final VendaTableModel vendasTableModel;

    // --- Tabela secundária de Itens (detalhes da venda selecionada) ---
    private final JLabel lblItens;
    private final JTable tabelaItens;
    private final ItensVendaTableModel itensTableModel;

    // --- Botões ---
    private final JButton btnAtualizar;

    public HistoricoVendasView(VendaTableModel vendasTableModel) {
        this.vendasTableModel = vendasTableModel;
        this.itensTableModel = new ItensVendaTableModel();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ==========================================
        // 1. CABEÇALHO
        // ==========================================
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

        // ==========================================
        // 2. PAINEL CENTRAL (Vendas + Itens)
        // ==========================================
        JPanel painelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // 2.1. Vendas (tabela principal)
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
        gbc.weightx = 1.0;
        painelCentral.add(painelVendas, gbc);

        // 2.2. Itens da Venda Selecionada (tabela de detalhes)
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
        gbc.weightx = 1.0;
        painelCentral.add(painelItens, gbc);

        add(painelCentral, BorderLayout.CENTER);

        // ==========================================
        // 3. RODAPÉ (Ações)
        // ==========================================
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Mensagem de status à esquerda
        JLabel lblStatus = new JLabel("Selecione uma venda na tabela para ver os detalhes.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        painelRodape.add(lblStatus, BorderLayout.WEST);

        // Botões à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(110, 35));
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        painelBotoes.add(btnAtualizar);
        painelRodape.add(painelBotoes, BorderLayout.EAST);

        add(painelRodape, BorderLayout.SOUTH);

        // ==========================================
        // 4. OUVINTES DE SELEÇÃO
        // ==========================================
        tabelaVendas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tabelaVendas.getSelectedRow();
                    if (selectedRow >= 0) {
                        Venda vendaSelecionada = vendasTableModel.getVendaAt(selectedRow);
                        if (vendaSelecionada != null) {
                            List<ItemVenda> itens = vendaSelecionada.getItens();
                            itensTableModel.setItens(itens);
                            lblItens.setText("Itens da venda #" + vendaSelecionada.getId() + ":");
                        }
                    } else {
                        itensTableModel.setItens(java.util.Collections.emptyList());
                        lblItens.setText("Itens da venda:");
                    }
                }
            }
        });
    }

    // ==========================================
    // MÉTODOS DE ATUALIZAÇÃO DA VIEW
    // ==========================================

    /**
     * Retorna o modelo de tabela de vendas.
     *
     * @return {@link VendaTableModel} associado a esta view.
     */
    public VendaTableModel getVendasTableModel() {
        return vendasTableModel;
    }

    /**
     * Retorna a tabela de vendas.
     *
     * @return {@link JTable} da tabela de vendas.
     */
    public JTable getTabelaVendas() {
        return tabelaVendas;
    }

    /**
     * Retorna o botão de atualizar.
     *
     * @return {@link JButton} btnAtualizar.
     */
    public JButton getBtnAtualizar() {
        return btnAtualizar;
    }
}
