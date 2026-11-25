package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ValidationException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.ClienteObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ClienteRepository;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Camada de Serviço de Negócio para {@link Cliente}.
 *
 */
public class ClienteService {

    private final ClienteRepository repository;
    private final List<ClienteObserver> observers;

    public ClienteService(ClienteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "ClienteRepository não pode ser nulo.");
        this.observers = new ArrayList<>();
    }

    public synchronized void addObserver(ClienteObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public synchronized void removeObserver(ClienteObserver observer) {
        observers.remove(observer);
    }

    public void notificarObservadores() {
        List<Cliente> listaAtualizada = repository.listarTodos();
        List<ClienteObserver> copiaObservers;
        synchronized (this) {
            copiaObservers = new ArrayList<>(observers);
        }
        SwingUtilities.invokeLater(() -> {
            for (ClienteObserver observer : copiaObservers) {
                try {
                    observer.onClientesChanged(Collections.unmodifiableList(listaAtualizada));
                } catch (Exception e) {
                    System.err.println("Erro ao notificar observador de clientes: " + e.getMessage());
                }
            }
        });
    }

    public Cliente salvar(Cliente cliente) {
        validarRegrasCliente(cliente);
        Cliente salvo = repository.salvar(cliente);
        notificarObservadores();
        return salvo;
    }

    public Cliente atualizar(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new ValidationException("O cliente deve possuir um ID para ser atualizado.");
        }
        validarRegrasCliente(cliente);
        Cliente atualizado = repository.atualizar(cliente);
        notificarObservadores();
        return atualizado;
    }

    public void excluir(Long id) {
        if (id == null) {
            throw new ValidationException("ID inválido para exclusão.");
        }
        repository.excluir(id);
        notificarObservadores();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return repository.buscarPorNome(nome);
    }

    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }

    private void validarRegrasCliente(Cliente cliente) {
        if (cliente == null) {
            throw new ValidationException("Dados do cliente não podem ser nulos.");
        }
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ValidationException("O Nome do Cliente é obrigatório.");
        }
    }
}