package br.gov.sp.cps.fatec.pdv.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade representando um Item de Venda (linha do pedido) no PDV.
 * <p>
 * <b>Conceito Pedagogico (Mapeamento Objeto-Relacional - ORM / JPA):</b>
 * <ul>
 * <li>{@link Entity}: Mapeia esta classe para a tabela {@code itens_venda}.</li>
 * <li>{@link ManyToOne}: Define o relacionamento N:1 com {@link Venda} (lado
 * "dono" da relacao, via {@code @JoinColumn}) e com {@link Produto}. O
 * {@code FetchType.LAZY} adia o carregamento da entidade associada ate o acesso,
 * evitando consultas desnecessarias.</li>
 * <li>{@link Column}: Mapeia propriedades para colunas com restricoes DDL
 * (not null, precisao decimal).</li>
 * </ul>
 * <p>
 * <b>Padrao MVC (camada Model):</b> Pertence a camada de dominio; nao acessa
 * View nem Controller.
 * <p>
 * <b>Padrao Repository:</b> Persistencia abstraida por {@code VendaRepository};
 * esta classe nao contem logica de acesso a dados.
 *
 * @author Jeniffer
 */
@Entity
@Table(name = "itens_venda")
public class ItemVenda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public ItemVenda() {
    }

    public ItemVenda(Venda venda, Produto produto, Integer quantidade, BigDecimal precoUnitario) {
        this.venda = venda;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemVenda that = (ItemVenda) o;
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (id != null) ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "ItemVenda{" +
                "id=" + id +
                ", produto=" + (produto != null ? produto.getNome() : null) +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
