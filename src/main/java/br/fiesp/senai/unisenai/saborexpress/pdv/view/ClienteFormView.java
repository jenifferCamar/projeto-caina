package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ClienteFormView extends JPanel {

    private final JLabel lblTitulo;
    private final JTextField txtId;
    private final JTextField txtNome;
    private final JTextField txtTelefone;
    private final JTextField txtObservacoes;
    private final JButton btnSalvar;
    private final JButton btnLimpar;
    private final JButton btnVoltar;

    private Long clienteIdAtual;

    public ClienteFormView() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));
        lblTitulo = new JLabel("Formulário de Cliente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        painelCabecalho.add(lblTitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Dados do Cliente "),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        painelCampos.add(new JLabel("ID (Automático):"), gbc);
        txtId = new JTextField(); txtId.setEditable(false); txtId.setEnabled(false);
        txtId.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        painelCampos.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        painelCampos.add(new JLabel("Nome (*):"), gbc);
        txtNome = new JTextField(); txtNome.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        painelCampos.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        painelCampos.add(new JLabel("Telefone:"), gbc);
        txtTelefone = new JTextField(); txtTelefone.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        painelCampos.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        painelCampos.add(new JLabel("Observações:"), gbc);
        txtObservacoes = new JTextField(); txtObservacoes.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        painelCampos.add(txtObservacoes, gbc);

        JPanel painelEnvelopador = new JPanel(new BorderLayout());
        painelEnvelopador.add(painelCampos, BorderLayout.NORTH);
        add(painelEnvelopador, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnVoltar = new JButton("<< Voltar para Listagem"); btnVoltar.setPreferredSize(new Dimension(180, 35));
        btnLimpar = new JButton("Limpar"); btnLimpar.setPreferredSize(new Dimension(110, 35));
        btnSalvar = new JButton("Salvar Cliente"); btnSalvar.setPreferredSize(new Dimension(150, 35));
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        painelBotoes.add(btnVoltar); painelBotoes.add(btnLimpar); painelBotoes.add(btnSalvar);
        add(painelBotoes, BorderLayout.SOUTH);

        configurarModoNovo();
    }

    public void configurarModoNovo() {
        this.clienteIdAtual = null;
        this.lblTitulo.setText("Novo Cliente - Cadastro");
        this.btnSalvar.setText("Cadastrar");
        limparCampos();
    }

    public void carregarParaEdicao(Cliente cliente) {
        if (cliente == null) { configurarModoNovo(); return; }
        this.clienteIdAtual = cliente.getId();
        this.lblTitulo.setText("Editar Cliente #" + cliente.getId());
        this.btnSalvar.setText("Salvar Alterações");
        this.txtId.setText(String.valueOf(cliente.getId()));
        this.txtNome.setText(cliente.getNome() != null ? cliente.getNome() : "");
        this.txtTelefone.setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
        this.txtObservacoes.setText(cliente.getObservacoes() != null ? cliente.getObservacoes() : "");
    }

    public void limparCampos() {
        this.txtId.setText("[Gerado após salvar]");
        this.txtNome.setText("");
        this.txtTelefone.setText("");
        this.txtObservacoes.setText("");
        this.txtNome.requestFocusInWindow();
    }

    public Long getClienteIdAtual() { return clienteIdAtual; }
    public String getNome() { return txtNome.getText().trim(); }
    public String getTelefone() { return txtTelefone.getText().trim(); }
    public String getObservacoes() { return txtObservacoes.getText().trim(); }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnLimpar() { return btnLimpar; }
    public JButton getBtnVoltar() { return btnVoltar; }
}
