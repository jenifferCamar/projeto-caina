package br.fiesp.senai.unisenai.saborexpress.pdv.model.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Item temporário do carrinho de compras da tela de Caixa.
 * <p>
 * <b>Conceito Pedagógico (DTO / Objeto de Transferência de Dados):</b>
 * <ul>
 * <li>Classe simples e transitória que representa um item adicionado ao carrinho
 * antes da finalização da venda.</li>
 * <li>Não é uma entidade JPA: não possui mapeamento de tabela e não é persistida
 * diretamente. Serve apenas como estrutura de dados entre a View ({@code CaixaView}),
 * o Controller ({@code VendaController}) e o Service ({@code VendaService}).</li>
 * <li>Armazena o ID do produto, a quantidade desejada e calcula o subtotal
 * com base no preço unitário informado.</li>
 * </ul>
 */
public class ItemCarrinho {

    private final Long produtoId;
    private final String nomeProduto;
    private final Integer quantidade;
    private final BigDecimal precoUnitario;

    /**
     * Construtor padrão.
     *
     * @param produtoId      Identificador do produto no banco de dados.
     * @param nomeProduto    Nome descritivo do produto (para exibição no carrinho).
     * @param quantidade     Quantidade de unidades do produto.
     * @param precoUnitario  Preço unitário do produto no momento da adição ao carrinho.
     */
    public ItemCarrinho(Long produtoId, String nomeProduto, Integer quantidade, BigDecimal precoUnitario) {
        this.produtoId = Objects.requireNonNull(produtoId, "ID do produto não pode ser nulo.");
        this.nomeProduto = Objects.requireNonNull(nomeProduto, "Nome do produto não pode ser nulo.");
        this.quantidade = Objects.requireNonNull(quantidade, "Quantidade não pode ser nula.");
        this.precoUnitario = Objects.requireNonNull(precoUnitario, "Preço unitário não pode ser nulo.");
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    /**
     * Calcula o subtotal do item (quantidade × preço unitário).
     *
     * @return Subtotal como {@link BigDecimal}.
     */
    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCarrinho that = (ItemCarrinho) o;
        return Objects.equals(produtoId, that.produtoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(produtoId);
    }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
