package br.fiesp.senai.unisenai.saborexpress.pdv.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Objeto de transferência de dados (DTO) imutável que agrega as métricas de
 * desempenho de vendas do PDV.
 * <p>
 * <b>Conceito Pedagógico (DTO / Value Object):</b>
 * <ul>
 * <li>Agrupa em uma única estrutura os três indicadores principais de vendas:
 * quantidade de vendas finalizadas, faturamento acumulado e ticket médio.</li>
 * <li>Imutável: uma vez criada, seus valores não podem ser alterados, o que
 * garante consistência entre leitura e exibição no dashboard.</li>
 * <li>Todos os campos numéricos monetários utilizam {@link BigDecimal} para
 * evitar problemas de precisão com ponto flutuante.</li>
 * </ul>
 */
public class MetricasVendasDTO {

    private final Long quantidadeVendas;
    private final BigDecimal faturamentoTotal;
    private final BigDecimal ticketMedio;
    private final LocalDateTime periodoInicio;
    private final LocalDateTime periodoFim;

    /**
     * Construtor recebendo todas as métricas calculadas.
     *
     * @param quantidadeVendas Número total de vendas finalizadas no período.
     * @param faturamentoTotal Soma dos totais das vendas finalizadas no período.
     * @param ticketMedio      Valor médio por venda (faturamento / quantidade).
     * @param periodoInicio    Data/hora inicial do filtro (pode ser {@code null} para
     *                         consulta geral sem filtro de período).
     * @param periodoFim       Data/hora final do filtro (pode ser {@code null} para
     *                         consulta geral sem filtro de período).
     */
    public MetricasVendasDTO(Long quantidadeVendas, BigDecimal faturamentoTotal,
                             BigDecimal ticketMedio, LocalDateTime periodoInicio,
                             LocalDateTime periodoFim) {
        this.quantidadeVendas = quantidadeVendas != null ? quantidadeVendas : 0L;
        this.faturamentoTotal = faturamentoTotal != null ? faturamentoTotal : BigDecimal.ZERO;
        this.ticketMedio = ticketMedio != null ? ticketMedio : BigDecimal.ZERO;
        this.periodoInicio = periodoInicio;
        this.periodoFim = periodoFim;
    }

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public BigDecimal getTicketMedio() {
        return ticketMedio;
    }

    public LocalDateTime getPeriodoInicio() {
        return periodoInicio;
    }

    public LocalDateTime getPeriodoFim() {
        return periodoFim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricasVendasDTO that = (MetricasVendasDTO) o;
        return Objects.equals(quantidadeVendas, that.quantidadeVendas)
                && Objects.equals(faturamentoTotal, that.faturamentoTotal)
                && Objects.equals(ticketMedio, that.ticketMedio)
                && Objects.equals(periodoInicio, that.periodoInicio)
                && Objects.equals(periodoFim, that.periodoFim);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantidadeVendas, faturamentoTotal, ticketMedio, periodoInicio, periodoFim);
    }

    @Override
    public String toString() {
        return "MetricasVendasDTO{" +
                "quantidadeVendas=" + quantidadeVendas +
                ", faturamentoTotal=" + faturamentoTotal +
                ", ticketMedio=" + ticketMedio +
                ", periodoInicio=" + periodoInicio +
                ", periodoFim=" + periodoFim +
                '}';
    }
}
