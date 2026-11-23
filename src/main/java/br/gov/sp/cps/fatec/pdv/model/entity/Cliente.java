package br.gov.sp.cps.fatec.pdv.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entidade de Domínio representando um Cliente do PDV (Sabor Express).
 *
 */

@Entity

@Table(name = "clientes")

@NamedQueries({
        @NamedQuery(name = "Cliente.listarTodos", query = "SELECT c FROM Cliente c ORDER BY c.nome ASC"),
        @NamedQuery(name = "Cliente.buscarPorNome", query = "SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
})
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    public Cliente() {
    }

    public Cliente(String nome, String telefone, String observacoes) {
        this.nome = nome;
        this.telefone = telefone;
        this.observacoes = observacoes;
    }

    public Cliente(Long id, String nome, String telefone, String observacoes) {
        this(nome, telefone, observacoes);
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        if (this.id != null && cliente.id != null) {
            return Objects.equals(this.id, cliente.id);
        }
        return Objects.equals(this.nome, cliente.nome) && Objects.equals(this.telefone, cliente.telefone);
    }

    @Override
    public int hashCode() {
        return (id != null) ? Objects.hash(id) : Objects.hash(nome, telefone);
    }

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", nome='" + nome + '\'' + ", telefone='" + telefone + '\'' + ", observacoes='" + observacoes + '\'' + '}';
    }
}