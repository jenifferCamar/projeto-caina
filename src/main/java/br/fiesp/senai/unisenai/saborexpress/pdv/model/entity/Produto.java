package br.fiesp.senai.unisenai.saborexpress.pdv.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidade de Domínio representando um Produto no Cardápio.
 * <p>
 * <b>Conceito Pedagógico (Mapeamento Objeto-Relacional - ORM):</b>
 * A JPA resolve o <i>Impedance Mismatch</i> (descompasso de impedância) entre o
 * paradigma orientado a objetos (classes, herança, encapsulamento) e o modelo relacional
 * (tabelas, chaves primárias, chaves estrangeiras).
 * <ul>
 * <li>{@link Entity}: Informa ao provedor JPA (Hibernate) que instâncias desta classe devem
 * ser mapeadas para uma tabela relacional.</li>
 * <li>{@link Table}: Define o nome e as restrições da tabela física.</li>
 * <li>{@link Id} e {@link GeneratedValue}: Define a chave primária com estratégia autoincremento
 * (IDENTITY), delegando ao mecanismo de sequence/identity do SGBD (H2).</li>
 * <li>{@link Column}: Mapeia propriedades para colunas com restrições DDL (not null, unique, precisão).</li>
 * <li>{@link ManyToOne}: Define relacionamento Many-to-One com {@link Categoria}.</li>
 * <li>Validações Bean Validation ({@link jakarta.validation.constraints}): Garantem integridade
 * de dados antes da persistência.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
@Entity
@Table(name = "produtos")
@NamedQueries({
        @NamedQuery(name = "Produto.listarTodos", query = "SELECT p FROM Produto p ORDER BY p.nome ASC"),
        @NamedQuery(name = "Produto.buscarPorCodigoBarras", query = "SELECT p FROM Produto p WHERE p.codigoBarras = :codigoBarras"),
        @NamedQuery(name = "Produto.listarAtivos", query = "SELECT p FROM Produto p WHERE (p.ativo IS NULL OR p.ativo = true) ORDER BY p.nome ASC"),
        @NamedQuery(name = "Produto.listarPorCategoria", query = "SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId AND (p.ativo IS NULL OR p.ativo = true) ORDER BY p.nome ASC")
})
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_barras", unique = true, nullable = false, length = 50)
    @NotBlank(message = "O Código de Barras é obrigatório")
    @Size(max = 50, message = "O Código de Barras deve ter no máximo 50 caracteres")
    private String codigoBarras;

    @Column(name = "nome", nullable = false, unique = true, length = 120)
    @NotBlank(message = "O Nome do Produto é obrigatório")
    @Size(max = 120, message = "O Nome do Produto deve ter no máximo 120 caracteres")
    private String nome;

    @Column(name = "descricao", length = 500)
    @Size(max = 500, message = "A Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    @NotNull(message = "O Preço do Produto é obrigatório")
    @DecimalMin(value = "0.01", message = "O Preço do Produto deve ser estritamente positivo (maior que zero)")
    private BigDecimal preco;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false, referencedColumnName = "id")
    @NotNull(message = "A Categoria é obrigatória")
    private Categoria categoria;

    @Column(name = "ativo", nullable = false)
    @NotNull(message = "O status Ativo/Inativo é obrigatório")
    private Boolean ativo = Boolean.TRUE;

    @Column(name = "estoque", nullable = false)
    @NotNull(message = "A Quantidade em Estoque é obrigatória")
    @Min(value = 0, message = "A Quantidade em Estoque não pode ser negativa")
    private Integer estoque;

    @Column(name = "estoque_minimo", nullable = false)
    @NotNull(message = "O Estoque Mínimo é obrigatório")
    @Min(value = 0, message = "O Estoque Mínimo não pode ser negativo")
    private Integer estoqueMinimo = 0;

    /**
     * Construtor padrão sem argumentos (exigência da especificação JPA/Hibernate
     * para instanciação via reflexão).
     */
    public Produto() {
    }

    /**
     * Construtor de conveniência para instanciação de novos produtos antes da
     * persistência (sem id).
     *
     * @param codigoBarras Código de barras único do produto.
     * @param nome         Nome descritivo do produto.
     * @param descricao    Descrição opcional do produto.
     * @param preco        Preço unitário de venda (deve ser positivo).
     * @param categoria    Categoria à qual o produto pertence.
     * @param estoque      Quantidade disponível em estoque (não-negativo).
     */
    public Produto(String codigoBarras, String nome, String descricao, BigDecimal preco,
                   Categoria categoria, Integer estoque, Integer estoqueMinimo) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.estoque = estoque;
        this.estoqueMinimo = (estoqueMinimo != null) ? estoqueMinimo : 0;
    }

    /**
     * Construtor completo incluindo o identificador.
     *
     * @param id           Identificador único gerado pelo banco de dados.
     * @param codigoBarras Código de barras único do produto.
     * @param nome         Nome descritivo do produto.
     * @param descricao    Descrição do produto.
     * @param preco        Preço unitário de venda.
     * @param categoria    Categoria do produto.
     * @param ativo        Flag indicando se o produto está ativo no cardápio.
     * @param estoque      Quantidade em estoque.
     */
    public Produto(Long id, String codigoBarras, String nome, String descricao, BigDecimal preco,
                   Categoria categoria, Boolean ativo, Integer estoque, Integer estoqueMinimo) {
        this(codigoBarras, nome, descricao, preco, categoria, estoque, estoqueMinimo);
        this.id = id;
        this.ativo = (ativo != null) ? ativo : Boolean.TRUE;
    }

    public Produto(String codigoBarras, String nome, String descricao, BigDecimal preco,
                   Categoria categoria, Boolean ativo, Integer estoque, Integer estoqueMinimo) {
        this(codigoBarras, nome, descricao, preco, categoria, estoque, estoqueMinimo);
        this.ativo = (ativo != null) ? ativo : Boolean.TRUE;
    }

    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    public void prePersist() {
        if (this.ativo == null) {
            this.ativo = Boolean.TRUE;
        }
        if (this.estoqueMinimo == null) {
            this.estoqueMinimo = 0;
        }
    }

    // ==========================================
    // CONSTRUTORES LEGADOS (Compatibilidade com código existente)
    // ==========================================

    /**
     * @deprecated Use {@link #Produto(String, String, String, BigDecimal, Categoria, Integer)} instead.
     * Construtor legado para compatibilidade com código que não usa Categoria e Descrição.
     */
    @Deprecated
    public Produto(String codigoBarras, String nome, BigDecimal preco, Integer estoque) {
        this(codigoBarras, nome, "", preco, null, estoque, 0);
    }

    /**
     * @deprecated Use {@link #Produto(Long, String, String, String, BigDecimal, Categoria, Boolean, Integer)} instead.
     * Construtor legado completo para compatibilidade.
     */
    @Deprecated
    public Produto(Long id, String codigoBarras, String nome, BigDecimal preco, Integer estoque) {
        this(codigoBarras, nome, preco, estoque);
        this.id = id;
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

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Boolean getAtivo() {
        return (ativo != null) ? ativo : Boolean.TRUE;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = (ativo != null) ? ativo : Boolean.TRUE;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.getAtivo());
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public Integer getEstoqueMinimo() {
        return (estoqueMinimo != null) ? estoqueMinimo : 0;
    }

    public void setEstoqueMinimo(Integer estoqueMinimo) {
        this.estoqueMinimo = (estoqueMinimo != null) ? estoqueMinimo : 0;
    }

    /**
     * Verifica se o estoque atual está abaixo ou igual ao estoque mínimo definido.
     *
     * @return {@code true} se o estoque estiver baixo (estoque <= estoqueMinimo),
     *         {@code false} caso contrário.
     */
    public boolean isEstoqueBaixo() {
        Integer estoqueAtual = (estoque != null) ? estoque : 0;
        Integer minimo = (estoqueMinimo != null) ? estoqueMinimo : 0;
        return estoqueAtual <= minimo;
    }

    /**
     * Verifica se o estoque atual está zerado.
     *
     * @return {@code true} se o estoque for igual a zero, {@code false} caso
     *         contrário.
     */
    public boolean isEstoqueZerado() {
        Integer estoqueAtual = (estoque != null) ? estoque : 0;
        return estoqueAtual == 0;
    }

    /**
     * @deprecated Use {@link #getEstoque()} instead.
     */
    @Deprecated
    public Integer getQuantidadeEstoque() {
        return estoque;
    }

    /**
     * @deprecated Use {@link #setEstoque(Integer)} instead.
     */
    @Deprecated
    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.estoque = quantidadeEstoque;
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
        Produto produto = (Produto) o;
        // Se ambos têm id, compara por id; caso contrário, compara pela chave natural
        // (codigoBarras ou nome)
        if (this.id != null && produto.id != null) {
            return Objects.equals(this.id, produto.id);
        }
        return Objects.equals(this.codigoBarras, produto.codigoBarras)
                && Objects.equals(this.nome, produto.nome);
    }

    @Override
    public int hashCode() {
        return (id != null) ? Objects.hash(id) : Objects.hash(codigoBarras, nome);
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", preco=" + preco +
                ", categoria=" + (categoria != null ? categoria.getNome() : "null") +
                ", ativo=" + ativo +
                ", estoque=" + estoque +
                ", estoqueMinimo=" + estoqueMinimo +
                '}';
    }
}