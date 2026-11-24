package br.fiesp.senai.unisenai.saborexpress.pdv.model.exception;

/**
 * Exceção de negócio lançada quando há tentativa de comercializar ou
 * adicionar ao carrinho um produto com status inativo.
 * <p>
 * Estende {@link IllegalStateException} para garantir validação preemptiva
 * e interoperabilidade com regras de estado da aplicação.
 */
public class ProdutoInativoException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public ProdutoInativoException(String message) {
        super(message);
    }

    public ProdutoInativoException(Long produtoId, String nomeProduto) {
        super(String.format("O produto '%s' (ID #%d) está inativo e não pode ser comercializado.", nomeProduto, produtoId));
    }
}
