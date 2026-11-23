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
 */
@Entity
@Table(name = "vendas")
@NamedQueries({
        @NamedQuery(name = "Venda.listarTodos", query = "SELECT v FROM Venda v ORDER BY v.dataHora DESC"),
        @NamedQuery(name = "Venda.buscarPorId", query = "SELECT v FROM Venda v LEFT JOIN FETCH v.itens WHERE v.id = :id")
})
public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==========================================
    // CAMPOS / COLUNAS (mapeamento ORM)
    // ==========================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Chave primaria auto-incremento (H2 IDENTITY)

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora; // Data/hora da venda (preenchida no construtor)

    // Cliente armazenado por id/nome (sem @ManyToOne Cliente) porque a entidade
    // Cliente (Card 2, w-tangly) ainda nao existe. Sera refatorado quando o
    // Card 2 for mergeado.
    @Column(name = "cliente_id")
    private Long clienteId; // FK opcional para cliente (null = venda sem cliente)

    @Column(name = "cliente_nome", length = 120)
    private String clienteNome; // Nome do cliente para exibicao no historico

    @Column(name = "forma_pagamento", length = 30)
    private String formaPagamento; // Dinheiro, Cartao, Pix etc. (refinado no Card 16)

    @Column(name = "status", length = 20, nullable = false)
    private String status = "FINALIZADA"; // Status da venda (refinado no Card 16)

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO; // Soma dos subtotais dos itens

    // Relacionamento 1:N com ItemVenda. cascade=ALL propaga persistencia/remocao.
    // orphanRemoval=true remove itens orfaos do banco ao serem removidos da lista.
    // FetchType.LAZY adia o carregamento dos itens ate o acesso (evita N+1).
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemVenda> itens = new ArrayList<>(); // Itens do pedido

    // ==========================================
    // CONSTRUTORES
    // ==========================================

    public Venda() {
        this.dataHora = LocalDateTime.now(); // Data/hora automatica na criacao
    }

    public Venda(Long clienteId, String clienteNome) {
        this();
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
    }

    // ==========================================
    // METODOS DE DOMINIO
    // ==========================================

    // Adiciona um item ao pedido, vincula a venda ao item e recalcula o total.
    public void adicionarItem(ItemVenda item) {
        item.setVenda(this); // vincula o item a esta venda (lado "dono")
        this.itens.add(item);
        recalcularTotal(); // atualiza o total apos adicionar
    }

    // Recalcula o total somando os subtotais de todos os itens.
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
