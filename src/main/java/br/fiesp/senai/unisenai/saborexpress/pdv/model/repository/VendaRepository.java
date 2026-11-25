package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import java.util.List;
import java.util.Optional;

public interface VendaRepository {

    Venda salvar(Venda venda);

    Venda atualizar(Venda venda);

    void excluir(Long id);

    Optional<Venda> buscarPorId(Long id);

    List<Venda> listarTodos();

    /**
     * Finaliza uma venda de forma atomica: persiste a Venda com seus itens
     * (cascade) e atualiza o estoque dos produtos vendidos, tudo em uma unica
     * transacao (commit/rollback).
     */
    Venda finalizarVenda(Venda venda);

    /**
     * Cancela uma venda finalizada de forma atomica: estorna o estoque dos
     * produtos e atualiza o status da venda para CANCELADA na mesma transacao.
     */
    Venda cancelarVenda(Long id);
}
