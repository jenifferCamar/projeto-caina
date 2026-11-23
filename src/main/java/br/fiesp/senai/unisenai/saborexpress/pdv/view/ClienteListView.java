package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.ClienteTableModel;

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
import java.util.List;

// Herança: ClienteListView estende JPanel, reutilizando comportamento de container Swing
public class ClienteListView extends JPanel {

    private final JLabel lblTitulo;
    private final JLabel lblTotalRegistros;
    private final JTable tabelaClientes;
    private final ClienteTableModel tableModel;

    private final JButton btnNovo;
    private final JButton btnEditar;
    private final JButton btnExcluir;
    private final JTextField txtBusca;
    private final JButton btnBuscar;
    private final JButton btnLimparFiltro;

    public ClienteListView() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));
        lblTitulo = new JLabel("Clientes - PDV");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblSubtitulo = new JLabel("Visualização em tempo real dos clientes persistidos no banco de dados.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);
        painelCabecalho.add(lblTitulo);
        painelCabecalho.add(Box.createVerticalStrut(4));
        painelCabecalho.add(lblSubtitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        tableModel = new ClienteTableModel();
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.setRowHeight(28);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaClientes.getTableHeader().setReorderingAllowed(false);

        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabelaClientes.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabelaClientes.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabelaClientes.getColumnModel().getColumn(3).setPreferredWidth(280);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Clientes Cadastrados "));
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        lblTotalRegistros = new JLabel("Total: 0 cliente(s)");
        lblTotalRegistros.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTotalRegistros.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelRodape.add(lblTotalRegistros, BorderLayout.WEST);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnRecarregar = new JButton("Recarregar"); btnRecarregar.setPreferredSize(new Dimension(110, 35));

        txtBusca = new JTextField(); txtBusca.setPreferredSize(new Dimension(180, 30));
        btnBuscar = new JButton("Buscar"); btnBuscar.setPreferredSize(new Dimension(90, 35));
        btnLimparFiltro = new JButton("Limpar Filtro"); btnLimparFiltro.setPreferredSize(new Dimension(110, 35));
        btnExcluir = new JButton("Excluir"); btnExcluir.setPreferredSize(new Dimension(110, 35));
        btnExcluir.setForeground(new Color(180, 40, 40));
        btnEditar = new JButton("Editar"); btnEditar.setPreferredSize(new Dimension(110, 35));
        btnNovo = new JButton("+ Novo Cliente"); btnNovo.setPreferredSize(new Dimension(150, 35));
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));
                painelBotoes.add(btnRecarregar);
        painelBotoes.add(txtBusca);
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnLimparFiltro);
        painelBotoes.add(btnExcluir); painelBotoes.add(btnEditar); painelBotoes.add(btnNovo);
        painelRodape.add(painelBotoes, BorderLayout.EAST);
        add(painelRodape, BorderLayout.SOUTH);
    }

    public void atualizarTabela(List<Cliente> clientes) {
        tableModel.setClientes(clientes);
        int total = (clientes != null) ? clientes.size() : 0;
        lblTotalRegistros.setText(String.format("Total: %d cliente(s) cadastrado(s)", total));
    }

    public Cliente getClienteSelecionado() {
        int selectedRow = tabelaClientes.getSelectedRow();
        if (selectedRow >= 0) {
            return tableModel.getClienteAt(selectedRow);
        }
        return null;
    }

    public JButton getBtnNovo() { return btnNovo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnExcluir() { return btnExcluir; }
    public JButton getBtnRecarregar() { return btnRecarregar; }
    public String getBuscaTexto() { return txtBusca.getText().trim(); }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JButton getBtnLimparFiltro() { return btnLimparFiltro; }
    public void limparBusca() { txtBusca.setText(""); }
}
