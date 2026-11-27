package br.fiesp.senai.unisenai.saborexpress.pdv.model.exception;

/**
 * Exceção de negócio lançada quando uma tentativa de venda excede a quantidade
 * física disponível em estoque para um produto.
 * <p>
 * <b>Conceito Pedagógico (Tratamento de Exceções de Domínio):</b>
 * <ul>
 * <li>Encapsula informações específicas sobre o produto e a quantidade
 * disponível, permitindo que a camada de apresentação construa mensagens de
 * erro amigáveis e contextualizadas.</li>
 * <li>É lançada estritamente na camada de serviço ({@link
 * br.fiesp.senai.unisenai.saborexpress.pdv.model.service.VendaService})
 * antes de qualquer persistência, garantindo que vendas impossíveis nunca
 * alcancem o banco de dados H2.</li>
 * </ul>
 */
public class EstoqueInsuficienteException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private final String nomeProduto;
    private final Integer estoqueDisponivel;
    private final Integer quantidadeSolicitada;

    /**
     * Construtor recebendo os dados do produto e as quantidades envolvidas.
     *
     * @param nomeProduto         Nome do produto com estoque insuficiente.
     * @param estoqueDisponivel   Quantidade atualmente disponível em estoque.
     * @param quantidadeSolicitada Quantidade que o usuário tentou vender.
     */
    public EstoqueInsuficienteException(String nomeProduto, Integer estoqueDisponivel,
                                        Integer quantidadeSolicitada) {
        super(montarMensagem(nomeProduto, estoqueDisponivel, quantidadeSolicitada));
        this.nomeProduto = nomeProduto;
        this.estoqueDisponivel = estoqueDisponivel != null ? estoqueDisponivel : 0;
        this.quantidadeSolicitada = quantidadeSolicitada != null ? quantidadeSolicitada : 0;
    }

    private static String montarMensagem(String nomeProduto, Integer estoqueDisponivel,
                                         Integer quantidadeSolicitada) {
        Integer disponivel = estoqueDisponivel != null ? estoqueDisponivel : 0;
        Integer solicitada = quantidadeSolicitada != null ? quantidadeSolicitada : 0;
        return String.format("Estoque insuficiente para '%s'. Disponível: %d, solicitado: %d.",
                nomeProduto, disponivel, solicitada);
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getEstoqueDisponivel() {
        return estoqueDisponivel;
    }

    public Integer getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }
}
