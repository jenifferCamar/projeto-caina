package br.fiesp.senai.unisenai.saborexpress.pdv.model.observer;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import java.util.List;

/**
 * Interface do Padrão de Projeto Observer (GoF - Comportamental) para notificações
 * de alterações em vendas.
 * <p>
 * <b>Conceito Pedagógico (Padrão Observer / Publish-Subscribe):</b>
 * <ul>
 * <li>Define uma relação um-para-muitos entre objetos, de forma que quando o
 * estado de uma venda se altera (finalização bem-sucedida), todos os
 * observadores registrados são notificados e atualizados automaticamente.</li>
 * <li>Promove o <b>baixo acoplamento</b>: a camada de negócio não precisa conhecer
 * os detalhes de implementação das telas Swing, emitindo apenas o evento de dados
 * modificados.</li>
 * <li>Utilizado pela {@link VendaService} para notificar a {@link VendaTableModel}
 * e outras visualizações de histórico após cada venda finalizada.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
@FunctionalInterface
public interface VendaObserver {

    /**
     * Notificação disparada quando ocorre uma nova venda finalizada com sucesso.
     *
     * @param vendas Lista atualizada e sincronizada de todas as vendas registradas.
     */
    void onVendasChanged(List<Venda> vendas);
}
