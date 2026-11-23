package br.gov.sp.cps.fatec.pdv.model.observer;

import br.gov.sp.cps.fatec.pdv.model.entity.Cliente;

import java.util.List;

/**
 * Interface do Padrão Observer (GoF) para a entidade {@link Cliente}.
 *
 */
@FunctionalInterface
public interface ClienteObserver {
    void onClientesChanged(List<Cliente> clientes);
}