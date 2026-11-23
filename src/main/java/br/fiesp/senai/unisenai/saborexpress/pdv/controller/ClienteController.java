package br.fiesp.senai.unisenai.saborexpress.pdv.controller;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.ClienteObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ClienteRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.ClienteService;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.ClienteFormView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.ClienteListView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.MainFrame;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Objects;

public class ClienteController implements ClienteObserver {

    private final MainFrame mainFrame;
    private final ClienteListView listView;
    private final ClienteFormView formView;
    private final ClienteService service;

    public ClienteController(MainFrame mainFrame, ClienteRepository repository) {
        this(mainFrame, new ClienteService(repository));
    }

    public ClienteController(MainFrame mainFrame, ClienteService service) {
        this.mainFrame = Objects.requireNonNull(mainFrame, "MainFrame não pode ser nulo.");
        this.listView = mainFrame.getClienteListView();
        this.formView = mainFrame.getClienteFormView();
        this.service = Objects.requireNonNull(service, "ClienteService não pode ser nulo.");
        vincularEventos();
    }

    public void inicializar() {
        this.service.addObserver(this);
        this.service.notificarObservadores();
    }

    private void vincularEventos() {
        listView.getBtnNovo().addActionListener(e -> onNovoCliente());
        listView.getBtnEditar().addActionListener(e -> onEditarCliente());
        listView.getBtnExcluir().addActionListener(e -> onExcluirCliente());
        listView.getBtnRecarregar().addActionListener(e -> onRecarregarClientes());
        formView.getBtnSalvar().addActionListener(e -> onSalvarCliente());
        formView.getBtnLimpar().addActionListener(e -> onLimparFormulario());
        formView.getBtnVoltar().addActionListener(e -> onVoltarParaListagem());
    }

    private void onNovoCliente() {
        formView.configurarModoNovo();
        mainFrame.mostrarFormularioCliente();
    }

    private void onVoltarParaListagem() {
        mainFrame.mostrarListaCliente();
    }

    private void onLimparFormulario() {
        formView.limparCampos();
    }

    private void onRecarregarClientes() {
        service.notificarObservadores();
    }

    private void onEditarCliente() {
        Cliente selecionado = listView.getClienteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um cliente na tabela para editar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        formView.carregarParaEdicao(selecionado);
        mainFrame.mostrarFormularioCliente();
    }

    private void onExcluirCliente() {
        Cliente selecionado = listView.getClienteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(mainFrame, "Selecione um cliente na tabela para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacao = JOptionPane.showConfirmDialog(mainFrame,
                "Deseja realmente excluir o cliente abaixo?\n\nID: #" + selecionado.getId() + "\nNome: " + selecionado.getNome(),
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                service.excluir(selecionado.getId());
                JOptionPane.showMessageDialog(mainFrame, "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Erro ao excluir cliente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSalvarCliente() {
        try {
            String nome = formView.getNome();
            String telefone = formView.getTelefone();
            String observacoes = formView.getObservacoes();
            Long id = formView.getClienteIdAtual();
            if (id == null) {
                Cliente novo = new Cliente(nome, telefone, observacoes);
                service.salvar(novo);
                JOptionPane.showMessageDialog(mainFrame, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                Cliente atualizado = new Cliente(id, nome, telefone, observacoes);
                service.atualizar(atualizado);
                JOptionPane.showMessageDialog(mainFrame, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            mainFrame.mostrarListaCliente();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(mainFrame, ex.getMessage(), "Validação de Dados", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Falha na operação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onClientesChanged(List<Cliente> clientes) {
        listView.atualizarTabela(clientes);
    }
}
