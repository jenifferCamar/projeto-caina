package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Padrao Repository para a entidade {@link Venda}.
 * <p>
 * <b>Conceito Pedagogico (Padrao Repository & Inversao de Dependencia - DIP):</b>
 * <ul>
 * <li>Abstrai a camada de acesso a dados (DAL), simulando uma colecao em memoria
 * de objetos de dominio.</li>
 * <li>Permite que as camadas superiores (Controller e Service) dependam de uma
 * <b>abstracao</b> (esta interface), e nao de uma implementacao concreta
 * acoplada a tecnologia de banco (JPA/Hibernate).</li>
 * <li>Facilita testes unitarios utilizando mocks/stubs e promove o Principio da
 * Responsabilidade Unica (SRP - SOLID).</li>
 * </ul>
 * <p>
 * <b>Padrao MVC:</b> Pertence a camada de persistencia (Model), isolada da View
 * e do Controller.
 *
 */
public interface VendaRepository {

    Venda salvar(Venda venda);

    Venda atualizar(Venda venda);

    void excluir(Long id);

    Optional<Venda> buscarPorId(Long id);

    List<Venda> listarTodos();

    /**
     * Finaliza uma venda de forma atomica: persiste a Venda com seus itens
     * (cascade) e atualiza o estoque dos produtos vendidos, tudo em uma unica
     * transacao (commit/rollback). Garante que nao haja estado parcial em caso
     * de falha.
     *
     * @param venda Venda contendo os itens e seus produtos/quantidades.
     * @return A venda persistida com ID gerado.
     * @throws RuntimeException caso ocorra falha na transacao (rollback aplicado).
     */
    Venda finalizarVenda(Venda venda);
}
