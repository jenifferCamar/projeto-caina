package br.gov.sp.cps.fatec.pdv.model.repository;

import br.gov.sp.cps.fatec.pdv.model.entity.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório para a entidade {@link Categoria}.
 * <p>
 * <b>Conceito Pedagógico (Repository Pattern):</b>
 * <ul>
 * <li>Define o contrato para operações de persistência de {@link Categoria}.</li>
 * <li>Não estende {@link org.springframework.data.jpa.repository.JpaRepository} para
 * evitar conflitos de versão com Spring Data JPA 3.x.</li>
 * <li>A implementação {@link CategoriaRepositoryImpl} fornece a lógica JPA manual.</li>
 * <li>A anotação {@link Repository} permite detecção automática via component scanning.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
@Repository
public interface CategoriaRepository {

    /**
     * Salva uma categoria (insert ou update).
     *
     * @param categoria Categoria a ser salva.
     * @return Categoria salva com ID gerado.
     */
    Categoria save(Categoria categoria);

    /**
     * Busca uma categoria por ID.
     *
     * @param id ID da categoria.
     * @return Optional contendo a categoria se encontrada.
     */
    Optional<Categoria> findById(Long id);

    /**
     * Lista todas as categorias.
     *
     * @return Lista de todas as categorias.
     */
    List<Categoria> findAll();

    /**
     * Exclui uma categoria por ID.
     *
     * @param id ID da categoria a ser excluída.
     */
    void deleteById(Long id);

    /**
     * Busca uma categoria pelo nome (case-insensitive).
     *
     * @param nome Nome da categoria.
     * @return Optional contendo a categoria encontrada.
     */
    Optional<Categoria> findByNomeIgnoreCase(String nome);

    /**
     * Verifica se existe uma categoria com o nome informado.
     *
     * @param nome Nome da categoria.
     * @return true se existir, false caso contrário.
     */
    boolean existsByNomeIgnoreCase(String nome);

    /**
     * Lista todas as categorias ativas, ordenadas pelo nome.
     *
     * @return Lista de categorias ativas.
     */
    List<Categoria> findByAtivaTrueOrderByNomeAsc();
}