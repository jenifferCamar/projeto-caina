package br.fiesp.senai.unisenai.saborexpress.pdv.model.observer;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import java.util.List;

/**
 * Interface do Padrao Observer para notificacao de alteracoes em Vendas.
 * <p>
 * <b>Conceito Pedagogico (Padrao Observer / Publish-Subscribe):</b>
 * Define uma relacao um-para-muitos entre objetos, de forma que quando o estado
 * do sujeito monitorado (Subject/Service) se altera, todos os observadores
 * registrados sao notificados e atualizados automaticamente.
 * <p>
 * <b>Thread Safety com Swing (EDT):</b> A notificacao deve ser despachada na
 * Event Dispatch Thread do Swing via SwingUtilities.invokeLater, evitando
 * condicoes de corrida e travamentos de UI.
 */
@FunctionalInterface
public interface VendaObserver {

    /**
     * Notificacao disparada quando ocorre alteracao no repositorio de vendas
     * (insercao, atualizacao, cancelamento bem-sucedido).
     *
     * @param vendas Lista atualizada e sincronizada de todas as vendas.
     */
    void onVendasChanged(List<Venda> vendas);
}
