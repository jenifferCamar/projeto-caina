package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.ProdutoTableModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;

/**
 * Visão (View) do Caixa de Vendas do PDV.
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC - Separação da Camada View):</b>
 * <ul>
 * <li>Esta classe é estritamente responsável pela apresentação gráfica da tela
 * de checkout e coleta de dados brutos digitados pelo usuário.</li>
 * <li><b>Não contém:</b> Acesso a banco de dados (JPA/SQL), regras de negócio
 * ou lógica transacional. Toda a conversação com o modelo passa pelo
 * {@code VendaController}.</li>
 * <li>Apresenta a listagem de produtos ativos do cardápio, permite adicionar
 * itens ao carrinho temporário, exibe o total parcial da venda e oferece campos
 * para seleção opcional de cliente e finalização do pedido.</li>
 * <li>Os botões e campos de entrada expõem seus ouvintes/ações diretamente ao
 * {@code VendaController}, preservando o Princípio de Inversão de Controle e
 * Responsabilidade Única (SOLID).</li>
 * </ul>
 */
public class CaixaView extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;
    private final JLabel lblTotal;

    // --- Tabela de Produtos Ativos (Cardápio) ---
    private final JLabel lblProdutosDisponiveis;
    private final JTable tabelaProdutos;
    private final ProdutoTableModel produtosTableModel;

    // --- Campos de Adição ao Carrinho ---
    private final JTextField txtQuantidade;
    private final JButton btnAdicionarCarrinho;

    // --- Tabela do Carrinho ---
    private final JLabel lblCarrinho;
    private final JTable tabelaCarrinho;
    private final DefaultTableModel carrinhoTableModel;
    private final JButton btnRemoverCarrinho;
    private final JButton btnLimparCarrinho;

    // --- Cliente e Total ---
    private final JTextField txtCliente;
    private final JButton btnFinalizar;
    private final JButton btnCancelar;
    private final JButton btnHistorico;

    public CaixaView() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ==========================================
        // 1. CABEÇALHO
        // ==========================================
        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));

        lblTitulo = new JLabel("Caixa - PDV");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblSubtitulo = new JLabel("Finalização de vendas de lanches do cardápio ativo.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);

        painelCabecalho.add(lblTitulo);
        painelCabecalho.add(Box.createVerticalStrut(4));
        painelCabecalho.add(lblSubtitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        // ==========================================
        // 2. PAINEL CENTRAL (Produtos + Carrinho)
        // ==========================================
        JPanel painelCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // 2.1. Produtos Disponíveis (Cardápio)
        JPanel painelProdutos = new JPanel(new BorderLayout(10, 10));
        painelProdutos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Produtos Disponíveis (Cardápio) "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        lblProdutosDisponiveis = new JLabel("Selecione um produto na tabela abaixo:");
        lblProdutosDisponiveis.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelProdutos.add(lblProdutosDisponiveis, BorderLayout.NORTH);

        produtosTableModel = new ProdutoTableModel();
        tabelaProdutos = new JTable(produtosTableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setRowHeight(28);
        tabelaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaProdutos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder(" Cardápio Ativo "));
        painelProdutos.add(scrollProdutos, BorderLayout.CENTER);

        // Painel de quantidade + adicionar
        JPanel painelAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblQtd = new JLabel("Quantidade:");
        lblQtd.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQuantidade = new JTextField("1");
        txtQuantidade.setPreferredSize(new Dimension(60, 30));
        btnAdicionarCarrinho = new JButton("Adicionar ao Carrinho");
        btnAdicionarCarrinho.setPreferredSize(new Dimension(170, 35));
        btnAdicionarCarrinho.setFont(new Font("Segoe UI", Font.BOLD, 12));

        painelAdd.add(lblQtd);
        painelAdd.add(txtQuantidade);
        painelAdd.add(btnAdicionarCarrinho);
        painelProdutos.add(painelAdd, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCentral.add(painelProdutos, gbc);

        // 2.2. Carrinho de Compras
        JPanel painelCarrinho = new JPanel(new BorderLayout(10, 10));
        painelCarrinho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Carrinho de Compras "),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        lblCarrinho = new JLabel("Itens temporários da venda:");
        lblCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painelCarrinho.add(lblCarrinho, BorderLayout.NORTH);

        // Tabela do carrinho (modelo simples de colunas)
        carrinhoTableModel = new DefaultTableModel(
                new Object[]{"Produto", "Qtd.", "Preço Unit.", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> String.class;
                    case 1 -> Integer.class;
                    case 2, 3 -> BigDecimal.class;
                    default -> Object.class;
                };
            }
        };
        tabelaCarrinho = new JTable(carrinhoTableModel);
        tabelaCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCarrinho.setRowHeight(26);
        tabelaCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaCarrinho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaCarrinho.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollCarrinho = new JScrollPane(tabelaCarrinho);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder(" Itens da Venda "));
        painelCarrinho.add(scrollCarrinho, BorderLayout.CENTER);

        // Botões do carrinho
        JPanel painelBotoesCarrinho = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnRemoverCarrinho = new JButton("Remover Item");
        btnRemoverCarrinho.setPreferredSize(new Dimension(130, 35));
        btnRemoverCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimparCarrinho = new JButton("Limpar Carrinho");
        btnLimparCarrinho.setPreferredSize(new Dimension(130, 35));
        btnLimparCarrinho.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLimparCarrinho.setForeground(new Color(180, 40, 40));

        painelBotoesCarrinho.add(btnRemoverCarrinho);
        painelBotoesCarrinho.add(btnLimparCarrinho);
        painelCarrinho.add(painelBotoesCarrinho, BorderLayout.SOUTH);

        gbc.gridx = 1;
        gbc.gridy = 0;
        painelCentral.add(painelCarrinho, gbc);

        add(painelCentral, BorderLayout.CENTER);

        // ==========================================
        // 3. RODAPÉ (Total, Cliente, Ações)
        // ==========================================
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Total à esquerda
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelRodape.add(lblTotal, BorderLayout.WEST);

        // Cliente no centro
        JPanel painelCliente = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel lblCliente = new JLabel("Cliente (opcional):");
        lblCliente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCliente = new JTextField();
        txtCliente.setPreferredSize(new Dimension(200, 30));
        txtCliente.setToolTipText("Nome do cliente para vínculo com a venda (opcional)");
        painelCliente.add(lblCliente);
        painelCliente.add(txtCliente);
        painelRodape.add(painelCliente, BorderLayout.CENTER);

        // Botões à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnHistorico = new JButton("Histórico de Vendas");
        btnHistorico.setPreferredSize(new Dimension(160, 35));
        btnHistorico.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnCancelar = new JButton("Cancelar Venda");
        btnCancelar.setPreferredSize(new Dimension(140, 35));
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnFinalizar = new JButton("Finalizar Venda");
        btnFinalizar.setPreferredSize(new Dimension(160, 35));
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        painelBotoes.add(btnHistorico);
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnFinalizar);
        painelRodape.add(painelBotoes, BorderLayout.EAST);

        add(painelRodape, BorderLayout.SOUTH);
    }

    // ==========================================
    // MÉTODOS DE ATUALIZAÇÃO DA VIEW
    // ==========================================

    /**
     * Atualiza a tabela de produtos disponíveis com a lista fornecida.
     *
     * @param produtos Coleção de produtos ativos do cardápio.
     */
    public void atualizarProdutos(List<Produto> produtos) {
        produtosTableModel.setProdutos(produtos);
    }

    /**
     * Adiciona um item ao modelo da tabela do carrinho.
     *
     * @param nomeProduto  Nome do produto.
     * @param quantidade   Quantidade de unidades.
     * @param precoUnitario Preço unitário do produto.
     * @param subtotal     Subtotal do item.
     */
    public void adicionarItemCarrinho(String nomeProduto, Integer quantidade,
                                      BigDecimal precoUnitario, BigDecimal subtotal) {
        carrinhoTableModel.addRow(new Object[]{nomeProduto, quantidade, precoUnitario, subtotal});
    }

    /**
     * Remove a linha selecionada da tabela do carrinho.
     */
    public void removerItemCarrinhoSelecionado() {
        int selectedRow = tabelaCarrinho.getSelectedRow();
        if (selectedRow >= 0) {
            carrinhoTableModel.removeRow(selectedRow);
        }
    }

    /**
     * Limpa todos os itens da tabela do carrinho.
     */
    public void limparCarrinho() {
        carrinhoTableModel.setRowCount(0);
    }

    /**
     * Atualiza o texto do total parcial exibido no rodapé.
     *
     * @param total Valor total formatado como string (ex: "R$ 45,90").
     */
    public void atualizarTotal(String total) {
        lblTotal.setText("Total: " + total);
    }

    /**
     * Retorna o produto selecionado na tabela de produtos disponíveis.
     *
     * @return {@link Produto} selecionado ou {@code null}.
     */
    public Produto getProdutoSelecionado() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow >= 0) {
            return produtosTableModel.getProdutoAt(selectedRow);
        }
        return null;
    }

    /**
     * Retorna o índice da linha selecionada na tabela do carrinho.
     *
     * @return Índice da linha selecionada, ou -1 se nenhuma estiver selecionada.
     */
    public int getCarrinhoSelectedRow() {
        return tabelaCarrinho.getSelectedRow();
    }

    /**
     * Retorna a quantidade digitada no campo de texto.
     *
     * @texto da quantidade.
     */
    public String getQuantidadeTexto() {
        return txtQuantidade.getText().trim();
    }

    /**
     * Retorna o nome do cliente digitado (pode estar vazio).
     *
     * @return Nome do cliente ou string vazia.
     */
    public String getClienteNome() {
        return txtCliente.getText().trim();
    }

    // ==========================================
    // GETTERS PARA COMPONENTES E OUVINTES
    // ==========================================

    public JTable getTabelaProdutos() {
        return tabelaProdutos;
    }

    public JTable getTabelaCarrinho() {
        return tabelaCarrinho;
    }

    public JTextField getTxtQuantidade() {
        return txtQuantidade;
    }

    public JTextField getTxtCliente() {
        return txtCliente;
    }

    public JButton getBtnAdicionarCarrinho() {
        return btnAdicionarCarrinho;
    }

    public JButton getBtnRemoverCarrinho() {
        return btnRemoverCarrinho;
    }

    public JButton getBtnLimparCarrinho() {
        return btnLimparCarrinho;
    }

    public JButton getBtnFinalizar() {
        return btnFinalizar;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }

    public JButton getBtnHistorico() {
        return btnHistorico;
    }

    public JLabel getLblTotal() {
        return lblTotal;
    }
}
