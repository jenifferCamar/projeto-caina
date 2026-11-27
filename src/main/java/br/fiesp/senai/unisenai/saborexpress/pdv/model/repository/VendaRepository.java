package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.MetricasVendasDTO;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository {

    Venda salvar(Venda venda);

    Venda atualizar(Venda venda);

    void excluir(Long id);

    Optional<Venda> buscarPorId(Long id);

    List<Venda> listarTodos();

    Venda finalizarVenda(Venda venda);

    /**
     * Cancela uma venda finalizada de forma atomica: estorna o estoque dos
     * produtos e atualiza o status da venda para CANCELADA na mesma transacao.
     */
    Venda cancelarVenda(Long id);

    // ==========================================
    // CONSULTAS AGREGADAS DE METRICAS DE VENDAS
    // ==========================================

    /**
     * Conta o total de vendas com status {@code FINALIZADA}.
     *
     * @return Quantidade de vendas finalizadas.
     */
    Long contarVendasFinalizadas();

    /**
     * Conta o total de vendas com status {@code FINALIZADA} em um período.
     *
     * @param inicio Data/hora inicial do filtro (inclusive).
     * @param fim    Data/hora final do filtro (inclusive).
     * @return Quantidade de vendas finalizadas no período.
     */
    Long contarVendasFinalizadas(LocalDateTime inicio, LocalDateTime fim);

    /**
     * Soma o campo {@code total} das vendas com status {@code FINALIZADA}.
     * Retorna {@code null} se não houver vendas finalizadas.
     *
     * @return Faturamento total acumulado.
     */
    BigDecimal somarFaturamentoFinalizado();

    /**
     * Soma o campo {@code total} das vendas com status {@code FINALIZADA} em um
     * período. Retorna {@code null} se não houver vendas finalizadas no período.
     *
     * @param inicio Data/hora inicial do filtro (inclusive).
     * @param fim    Data/hora final do filtro (inclusive).
     * @return Faturamento total acumulado no período.
     */
    BigDecimal somarFaturamentoFinalizado(LocalDateTime inicio, LocalDateTime fim);
}
