package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public static final String TELA_LISTA = "CARD_LISTA";
    public static final String TELA_FORMULARIO = "CARD_FORMULARIO";
    public static final String TELA_CLIENTE_LISTA = "CARD_CLIENTE_LISTA";
    public static final String TELA_CLIENTE_FORMULARIO = "CARD_CLIENTE_FORMULARIO";
    public static final String TELA_CAIXA = "CARD_CAIXA";
    public static final String TELA_HISTORICO = "CARD_HISTORICO";

    private final CardLayout cardLayout;
    private final JPanel painelPrincipal;

    private final ProdutoListView listView;
    private final ProdutoFormView formView;
    private final ClienteListView clienteListView;
    private final ClienteFormView clienteFormView;
    private final CaixaView caixaView;
    private final HistoricoVendasView historicoView;

    public MainFrame() {
        super("Sabor Express PDV - Gestão de Lanchonete");

        this.cardLayout = new CardLayout();
        this.painelPrincipal = new JPanel(cardLayout);

        this.listView = new ProdutoListView();
        this.formView = new ProdutoFormView();
        this.clienteListView = new ClienteListView();
        this.clienteFormView = new ClienteFormView();
        this.caixaView = new CaixaView();
        this.historicoView = new HistoricoVendasView();

        this.painelPrincipal.add(listView, TELA_LISTA);
        this.painelPrincipal.add(formView, TELA_FORMULARIO);
        this.painelPrincipal.add(clienteListView, TELA_CLIENTE_LISTA);
        this.painelPrincipal.add(clienteFormView, TELA_CLIENTE_FORMULARIO);
        this.painelPrincipal.add(caixaView, TELA_CAIXA);
        this.painelPrincipal.add(historicoView, TELA_HISTORICO);

        JPanel painelNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCardapio = new JButton("Cardápio");
        JButton btnClientes = new JButton("Clientes");
        JButton btnCaixa = new JButton("Caixa");
        JButton btnHistorico = new JButton("Histórico");
        btnCardapio.addActionListener(e -> mostrarLista());
        btnClientes.addActionListener(e -> mostrarListaCliente());
        btnCaixa.addActionListener(e -> mostrarCaixa());
        btnHistorico.addActionListener(e -> mostrarHistorico());
        painelNav.add(btnCardapio);
        painelNav.add(btnClientes);
        painelNav.add(btnCaixa);
        painelNav.add(btnHistorico);

        JPanel container = new JPanel(new BorderLayout());
        container.add(painelNav, BorderLayout.NORTH);
        container.add(painelPrincipal, BorderLayout.CENTER);
        setContentPane(container);

        // Barra de menus superior para alternar facilmente entre os módulos
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuModulos = new javax.swing.JMenu("Módulos");

        javax.swing.JMenuItem itemCaixa = new javax.swing.JMenuItem("Caixa de Vendas (PDV)");
        itemCaixa.addActionListener(e -> mostrarCaixa());

        javax.swing.JMenuItem itemProdutos = new javax.swing.JMenuItem("Gerenciamento de Produtos");
        itemProdutos.addActionListener(e -> mostrarLista());

        javax.swing.JMenuItem itemHistorico = new javax.swing.JMenuItem("Histórico de Vendas");
        itemHistorico.addActionListener(e -> mostrarHistorico());

        javax.swing.JMenuItem itemClientes = new javax.swing.JMenuItem("Gerenciamento de Clientes");
        itemClientes.addActionListener(e -> mostrarListaCliente());

        menuModulos.add(itemCaixa);
        menuModulos.add(itemProdutos);
        menuModulos.add(itemHistorico);
        menuModulos.add(itemClientes);
        menuBar.add(menuModulos);
        setJMenuBar(menuBar);

        // Configurações da Janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setPreferredSize(new Dimension(1000, 700));
        pack();
        setLocationRelativeTo(null);
    }

    public void mostrarLista() { cardLayout.show(painelPrincipal, TELA_LISTA); }
    public void mostrarFormulario() { cardLayout.show(painelPrincipal, TELA_FORMULARIO); }
    public void mostrarListaCliente() { cardLayout.show(painelPrincipal, TELA_CLIENTE_LISTA); }
    public void mostrarFormularioCliente() { cardLayout.show(painelPrincipal, TELA_CLIENTE_FORMULARIO); }
    public void mostrarCaixa() { cardLayout.show(painelPrincipal, TELA_CAIXA); }
    public void mostrarHistorico() { cardLayout.show(painelPrincipal, TELA_HISTORICO); }

    public ProdutoListView getListView() { return listView; }
    public ProdutoFormView getFormView() { return formView; }
    public ClienteListView getClienteListView() { return clienteListView; }
    public ClienteFormView getClienteFormView() { return clienteFormView; }
    public CaixaView getCaixaView() { return caixaView; }
    public HistoricoVendasView getHistoricoView() { return historicoView; }
}