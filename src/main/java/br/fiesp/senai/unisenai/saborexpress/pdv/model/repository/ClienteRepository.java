package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Padrão Repository para a entidade {@link Cliente}.
 *
 */
public interface ClienteRepository {
    Cliente salvar(Cliente cliente);
    Cliente atualizar(Cliente cliente);
    void excluir(Long id);
    Optional<Cliente> buscarPorId(Long id);
    List<Cliente> buscarPorNome(String nome);
    List<Cliente> listarTodos();
}