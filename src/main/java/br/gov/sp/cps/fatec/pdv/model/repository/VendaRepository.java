package br.gov.sp.cps.fatec.pdv.model.repository;

import br.gov.sp.cps.fatec.pdv.model.entity.Venda;

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
}
