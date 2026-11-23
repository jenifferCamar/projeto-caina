package br.fiesp.senai.unisenai.saborexpress.pdv.model.observer;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;

import java.util.List;

/**
 * Interface do Padrão Observer (GoF) para a entidade {@link Cliente}.
 *
 */
@FunctionalInterface
public interface ClienteObserver {
    void onClientesChanged(List<Cliente> clientes);
}