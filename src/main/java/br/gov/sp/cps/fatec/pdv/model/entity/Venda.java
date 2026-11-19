package br.gov.sp.cps.fatec.pdv.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entidade representando uma Venda (pedido finalizado) no PDV.
 * <p>
 * <b>Conceito Pedagogico (Mapeamento Objeto-Relacional - ORM / JPA):</b>
 * <ul>
 * <li>{@link Entity}: Informa ao provedor JPA (Hibernate) que instancias desta
 * classe devem ser mapeadas para uma tabela relacional ({@code vendas}).</li>
 * <li>{@link Table}: Define o nome fisico da tabela no banco de dados.</li>
 * <li>{@link Id} e {@link GeneratedValue}: Define a chave primaria com
 * estrategia autoincremento (IDENTITY), delegando ao mecanismo do SGBD (H2).</li>
 * <li>{@link OneToMany}: Mapeia o relacionamento 1:N entre Venda e ItemVenda.
 * O {@code mappedBy} indica que o lado "dono" da relacao e o campo {@code venda}
 * em {@link ItemVenda}. O {@code cascade = ALL} propaga persistencia/remocao
 * para os itens. O {@code orphanRemoval = true} remove itens orfaos do banco
 * quando removidos da lista.</li>
 * <li>{@link Column}: Mapeia propriedades para colunas com restricoes DDL.</li>
 * </ul>
 * <p>
 * <b>Padrao MVC (camada Model):</b> Esta entidade pertence a camada de dominio/modelo.
 * Nao acessa View nem Controller, preservando a separacao de responsabilidades.
 * <p>
 * <b>Padrao Repository:</b> O acesso a dados e abstraido pela interface
 * {@code VendaRepository}; esta classe nao contem logica de persistencia.
 * <p>
 * <b>Observer:</b> A notificacao reativa de alteracoes (ex.: atualizacao de
 * tabelas Swing apos uma venda) sera implementada na Service (Card 6) e na View
 * (Cards 5/7/8), consumindo os dados desta entidade.
 *
 * @author Jeniffer
 */
@Entity
@Table(name = "vendas")
@NamedQueries({
        @NamedQuery(name = "Venda.listarTodos", query = "SELECT v FROM Venda v ORDER BY v.dataHora DESC"),
        @NamedQuery(name = "Venda.buscarPorId", query = "SELECT v FROM Venda v LEFT JOIN FETCH v.itens WHERE v.id = :id")
})
public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "cliente_nome", length = 120)
    private String clienteNome;

    @Column(name = "forma_pagamento", length = 30)
    private String formaPagamento;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "FINALIZADA";

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVenda> itens = new ArrayList<>();

    public Venda() {
        this.dataHora = LocalDateTime.now();
    }

    public Venda(Long clienteId, String clienteNome) {
        this();
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
    }

    public void adicionarItem(ItemVenda item) {
        item.setVenda(this);
        this.itens.add(item);
        recalcularTotal();
    }

    public void recalcularTotal() {
        BigDecimal soma = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            if (item.getSubtotal() != null) {
                soma = soma.add(item.getSubtotal());
            }
        }
        this.total = soma;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venda venda = (Venda) o;
        if (this.id != null && venda.id != null) {
            return Objects.equals(this.id, venda.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (id != null) ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "Venda{" +
                "id=" + id +
                ", dataHora=" + dataHora +
                ", clienteNome='" + clienteNome + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", itens=" + itens.size() +
                '}';
    }
}
