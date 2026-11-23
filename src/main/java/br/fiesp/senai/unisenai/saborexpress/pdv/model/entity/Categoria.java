package br.fiesp.senai.unisenai.saborexpress.pdv.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * Entidade de Domínio representando uma Categoria de Produtos no Cardápio.
 * <p>
 * Implementada como Entidade JPA (não Enum) para permitir futura expansão
 * e gerenciamento de categorias via banco de dados.
 * <p>
 * Valores padrão iniciais: Lanche, Bebida, Acompanhamento, Sobremesa.
 *
 * @author Cainã Antunes Silva
 */
@Entity
@Table(name = "categorias")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, unique = true, length = 60)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = Boolean.TRUE;

    /**
     * Construtor padrão sem argumentos (exigência da especificação JPA/Hibernate
     * para instanciação via reflexão).
     */
    public Categoria() {
    }

    /**
     * Construtor de conveniência para criação de novas categorias.
     *
     * @param nome      Nome da categoria (obrigatório, único).
     * @param descricao Descrição opcional da categoria.
     */
    public Categoria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    /**
     * Construtor completo incluindo o identificador.
     *
     * @param id          Identificador único gerado pelo banco de dados.
     * @param nome        Nome da categoria.
     * @param descricao   Descrição da categoria.
     * @param ativa       Flag indicando se a categoria está ativa.
     */
    public Categoria(Long id, String nome, String descricao, Boolean ativa) {
        this(nome, descricao);
        this.id = id;
        this.ativa = ativa;
    }

    // ==========================================
    // GETTERS E SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    // ==========================================
    // EQUALS, HASHCODE E TOSTRING
    // ==========================================

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Categoria categoria = (Categoria) o;
        if (this.id != null && categoria.id != null) {
            return Objects.equals(this.id, categoria.id);
        }
        return Objects.equals(this.nome, categoria.nome);
    }

    @Override
    public int hashCode() {
        return (id != null) ? Objects.hash(id) : Objects.hash(nome);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", ativa=" + ativa +
                '}';
    }
}