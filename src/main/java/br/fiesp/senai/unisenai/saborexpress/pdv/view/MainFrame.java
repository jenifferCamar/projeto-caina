package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Janela Principal da Aplicação Swing ({@link JFrame}).
 * <p>
 * <b>Conceito Pedagógico (Padrão CardLayout & Interface de Janela Única -
 * SDI):</b>
 * <ul>
 * <li>Em vez de abrir múltiplas janelas flutuantes desordenadas (MDI ou
 * múltiplos JFrames),
 * o {@link CardLayout} gerencia diferentes "páginas" ou painéis de visão em uma
 * única área
 * de exibição como se fossem cartas empilhadas.</li>
 * <li>A orquestração e a decisão de qual tela deve ser exibida é controlada
 * pelo
 * {@code ProdutoController}, garantindo a independência das visões entre
 * si.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class MainFrame extends JFrame {

    public static final String TELA_LISTA = "CARD_LISTA";
    public static final String TELA_FORMULARIO = "CARD_FORMULARIO";
    public static final String TELA_CAIXA = "CARD_CAIXA";

    private final CardLayout cardLayout;
    private final JPanel painelPrincipal;

    private final ProdutoListView listView;
    private final ProdutoFormView formView;
    private final CaixaView caixaView;

    private final ClienteListView clienteListView;
    private final ClienteFormView clienteFormView;

    public MainFrame() {
        super("PDV - Gestão de Produtos");

        this.cardLayout = new CardLayout();
        this.painelPrincipal = new JPanel(cardLayout);

        this.listView = new ProdutoListView();
        this.formView = new ProdutoFormView();
        this.caixaView = new CaixaView();

        this.clienteListView = new ClienteListView();
        this.clienteFormView = new ClienteFormView();

        // Adiciona as telas ao container gerenciado por CardLayout
        this.painelPrincipal.add(listView, TELA_LISTA);
        this.painelPrincipal.add(formView, TELA_FORMULARIO);
        this.painelPrincipal.add(caixaView, TELA_CAIXA);
        this.painelPrincipal.add(clienteListView, "CARD_CLIENTE_LISTA");
        this.painelPrincipal.add(clienteFormView, "CARD_CLIENTE_FORMULARIO");

        setContentPane(painelPrincipal);

        // Barra de menus superior para alternar facilmente entre os módulos
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuModulos = new javax.swing.JMenu("Módulos");

        javax.swing.JMenuItem itemCaixa = new javax.swing.JMenuItem("Caixa de Vendas (PDV)");
        itemCaixa.addActionListener(e -> mostrarCaixa());

        javax.swing.JMenuItem itemProdutos = new javax.swing.JMenuItem("Gerenciamento de Produtos");
        itemProdutos.addActionListener(e -> mostrarLista());

        javax.swing.JMenuItem itemClientes = new javax.swing.JMenuItem("Gerenciamento de Clientes");
        itemClientes.addActionListener(e -> mostrarListaCliente());

        menuModulos.add(itemCaixa);
        menuModulos.add(itemProdutos);
        menuModulos.add(itemClientes);
        menuBar.add(menuModulos);
        setJMenuBar(menuBar);
        // Configurações da Janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 600));
        setPreferredSize(new Dimension(950, 650));
        pack();
        setLocationRelativeTo(null); // Centraliza a janela na tela
    }

    /**
     * Alterna a visualização para a tela de listagem de produtos.
     */
    public void mostrarLista() {
        cardLayout.show(painelPrincipal, TELA_LISTA);
    }

    /**
     * Alterna a visualização para a tela de formulário (cadastro/edição).
     */
    public void mostrarFormulario() {
        cardLayout.show(painelPrincipal, TELA_FORMULARIO);
    }

    public void mostrarListaCliente() {
        cardLayout.show(painelPrincipal, "CARD_CLIENTE_LISTA");
    }

    public void mostrarFormularioCliente() {
        cardLayout.show(painelPrincipal, "CARD_CLIENTE_FORMULARIO");
    }

    public ClienteListView getClienteListView() {
        return clienteListView;
    }

    public ClienteFormView getClienteFormView() {
        return clienteFormView;
    }

    public ProdutoListView getListView() {
        return listView;
    }

    public ProdutoFormView getFormView() {
        return formView;
    }

    public CaixaView getCaixaView() {
        return caixaView;
    }

    /**
     * Alterna a visualização para a tela de Caixa (checkout de vendas).
     */
    public void mostrarCaixa() {
        cardLayout.show(painelPrincipal, TELA_CAIXA);
    }
}
