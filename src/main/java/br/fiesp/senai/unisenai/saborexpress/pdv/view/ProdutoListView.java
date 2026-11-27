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
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Visão (View) da Listagem de Produtos em formato tabular ({@link JTable}).
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC & Componentes Reativos do Swing):</b>
 * <ul>
 * <li>Apresenta a listagem atualizada de produtos persistidos no banco de
 * dados.</li>
 * <li>O desacoplamento é garantido através do {@link ProdutoTableModel}: a
 * tabela é atualizada
 * automaticamente sempre que novas listas são fornecidas pelo Controller.</li>
 * <li>Expõe botões de comando ("Novo Produto", "Editar", "Excluir") e método
 * para identificar
 * o registro atualmente selecionado pelo usuário.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class ProdutoListView extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblTotalRegistros;
    private final JTable tabelaProdutos;
    private final ProdutoTableModel tableModel;

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JButton btnRecarregar;
    private final JTextField txtBusca;
    private final JButton btnBuscar;
    private final JButton btnLimparFiltro;

    public ProdutoListView() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 1. Cabeçalho Didático
        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));

        lblTitulo = new JLabel("Catálogo de Produtos - PDV");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSubtitulo = new JLabel("Visualização em tempo real dos produtos persistidos no banco de dados.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);

        painelCabecalho.add(lblTitulo);
        painelCabecalho.add(Box.createVerticalStrut(4));
        painelCabecalho.add(lblSubtitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        // 2. Tabela Central com ScrollPane
        tableModel = new ProdutoTableModel();
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.setRowHeight(28);
        tabelaProdutos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaProdutos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);

        // Ajuste didático de largura das colunas
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(120); // Código de Barras
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(240); // Nome
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(100); // Preço
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Qtd. Estoque
        tabelaProdutos.getColumnModel().getColumn(5).setPreferredWidth(80);  // Estoque Mínimo
        tabelaProdutos.getColumnModel().getColumn(6).setPreferredWidth(70);  // Status
        tabelaProdutos.getColumnModel().getColumn(7).setPreferredWidth(80);  // Alerta Estoque

        // Renderer customizado para destacar visualmente estoque zerado ou baixo
        tabelaProdutos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final Color COR_ZERADO = new Color(180, 40, 40); // vermelho escuro
            private static final Color COR_BAIXO = new Color(210, 130, 0);  // laranja
            // Cores explícitas de seleção (branco no azul padrão do Swing)
            private static final Color SELECTION_FOREGROUND = Color.WHITE;
            private static final Color SELECTION_BACKGROUND = new Color(51, 153, 255); // Azul padrão de seleção

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    // Força cores de seleção explícitas: texto branco no azul de seleção
                    c.setForeground(Color.WHITE);
                    c.setBackground(new Color(51, 153, 255));
                    // Garante fonte normal (não negrito) quando selecionado
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                } else {
                    // No estado normal, aplica cores baseadas no estoque
                    c.setBackground(Color.WHITE); // Garante fundo branco no estado normal
                    c.setFont(c.getFont().deriveFont(Font.PLAIN)); // Fonte normal
                    Produto produto = tableModel.getProdutoAt(row);
                    if (produto != null) {
                        if (produto.isEstoqueZerado()) {
                            c.setForeground(COR_ZERADO);
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else if (produto.isEstoqueBaixo()) {
                            c.setForeground(COR_BAIXO);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }

                return c;
            }
        });

        // Botão Recarregar posicionado no canto superior direito da tabela
        btnRecarregar = new JButton("Recarregar");
        btnRecarregar.setPreferredSize(new Dimension(110, 30));
        btnRecarregar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Campo de busca ao lado do botão Recarregar
        txtBusca = new JTextField();
        txtBusca.setMinimumSize(new Dimension(150, 30));
        txtBusca.setPreferredSize(new Dimension(220, 30));
        txtBusca.setToolTipText("Digite o nome ou código de barras para buscar (busca automática ao digitar)");
        btnLimparFiltro = new JButton("Limpar Filtro");

        JPanel painelAcoesTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        painelAcoesTabela.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
        painelAcoesTabela.add(txtBusca);
        painelAcoesTabela.add(btnLimparFiltro);
        painelAcoesTabela.add(btnRecarregar);

        JPanel painelWrapperTabela = new JPanel(new BorderLayout());
        painelWrapperTabela.add(painelAcoesTabela, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Produtos Cadastrados "));
        painelWrapperTabela.add(scrollPane, BorderLayout.CENTER);
        add(painelWrapperTabela, BorderLayout.CENTER);

        // 3. Rodapé com Ações e Contador de Registros
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Contador de registros à esquerda
        JPanel painelContador = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        lblTotalRegistros = new JLabel("Total: 0 produto(s)");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTotalRegistros.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        painelContador.add(lblTotalRegistros);
        painelRodape.add(painelContador, BorderLayout.WEST);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));

        btnExcluir = new JButton("Excluir");
        btnExcluir.setForeground(new Color(180, 40, 40));

        btnEditar = new JButton("Editar");

        btnNovo = new JButton("+ Novo Produto");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnNovo);

        painelRodape.add(painelBotoes, BorderLayout.EAST);
        add(painelRodape, BorderLayout.SOUTH);
        add(painelRodape, BorderLayout.SOUTH);
    }

    /**
     * Atualiza a tabela com a lista atual de produtos e renova o contador do
     * rodapé.
     *
     * @param produtos Coleção de produtos a exibir.
     */
    public void atualizarTabela(List<Produto> produtos) {
        tableModel.setProdutos(produtos);
        int total = (produtos != null) ? produtos.size() : 0;
        lblTotalRegistros.setText(String.format("Total: %d produto(s) cadastrado(s)", total));
    }

    /**
     * Retorna a entidade {@link Produto} selecionada na linha ativa da tabela.
     *
     * @return {@link Produto} selecionado ou {@code null} se nenhuma linha estiver
     *         selecionada.
     */
    public Produto getProdutoSelecionado() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow >= 0) {
            return tableModel.getProdutoAt(selectedRow);
        }
        return null;
    }

    // ==========================================
    // GETTERS PARA COMPONENTES E OUVINTES
    // ==========================================

    public JTable getTabelaProdutos() {
        return tabelaProdutos;
    }

    public ProdutoTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnNovo() {
        return btnNovo;
    }

    public JButton getBtnEditar() {
        return btnEditar;
    }

    public JButton getBtnExcluir() {
        return btnExcluir;
    }

    public JButton getBtnRecarregar() {
        return btnRecarregar;
    }

    public String getBuscaTexto() {
        return txtBusca.getText().trim();
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnLimparFiltro() {
        return btnLimparFiltro;
    }

    public void limparBusca() {
        txtBusca.setText("");
    }
}
