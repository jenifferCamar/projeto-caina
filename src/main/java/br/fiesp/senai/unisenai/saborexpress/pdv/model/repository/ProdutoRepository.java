package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface do Repositório para a entidade {@link Produto}.
 * <p>
 * <b>Conceito Pedagógico (Repository Pattern):</b>
 * <ul>
 * <li>Define o contrato para operações de persistência de {@link Produto}.</li>
 * <li>Não estende {@link org.springframework.data.jpa.repository.JpaRepository} para
 * evitar conflitos de versão com Spring Data JPA 3.x.</li>
 * <li>A implementação {@link ProdutoRepositoryImpl} fornece a lógica JPA manual.</li>
 * <li>A anotação {@link Repository} permite detecção automática via component scanning.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
@Repository
public interface ProdutoRepository {

    /**
     * Salva um produto (insert ou update).
     *
     * @param produto Produto a ser salvo.
     * @return Produto salvo com ID gerado.
     */
    Produto save(Produto produto);

    /**
     * Busca um produto por ID.
     *
     * @param id ID do produto.
     * @return Optional contendo o produto se encontrado.
     */
    Optional<Produto> findById(Long id);

    /**
     * Lista todos os produtos.
     *
     * @return Lista de todos os produtos.
     */
    List<Produto> findAll();

    /**
     * Exclui um produto por ID.
     *
     * @param id ID do produto a ser excluído.
     */
    void deleteById(Long id);

    /**
     * Busca um produto pelo seu código de barras único.
     *
     * @param codigoBarras Código de barras a ser consultado.
     * @return Optional contendo o produto encontrado, ou vazio se não existir.
     */
    Optional<Produto> findByCodigoBarras(String codigoBarras);

    /**
     * Verifica se existe um produto com o código de barras informado.
     *
     * @param codigoBarras Código de barras a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByCodigoBarras(String codigoBarras);

    /**
     * Verifica se existe um produto com o nome informado (para validação de unicidade).
     *
     * @param nome Nome do produto a ser verificado.
     * @return true se existir, false caso contrário.
     */
    boolean existsByNome(String nome);

    /**
     * Verifica se existe outro produto com o mesmo nome (excluindo o ID informado).
     * Útil para validação de unicidade durante atualização.
     *
     * @param nome Nome do produto a ser verificado.
     * @param id   ID do produto a ser ignorado na verificação.
     * @return true se existir outro produto com o mesmo nome, false caso contrário.
     */
    boolean existsByNomeAndIdNot(String nome, Long id);

    /**
     * Lista todos os produtos ativos.
     *
     * @return Lista de produtos ativos.
     */
    List<Produto> findByAtivoTrue();

    /**
     * Lista produtos ativos de uma determinada categoria.
     *
     * @param categoria Categoria dos produtos.
     * @return Lista de produtos ativos da categoria.
     */
    List<Produto> findByAtivoTrueAndCategoria(Categoria categoria);

    /**
     * Lista todos os produtos ativos, ordenados alfabeticamente pelo nome.
     *
     * @return Lista de produtos ativos.
     */
    List<Produto> findByAtivoTrueOrderByNomeAsc();

    /**
     * Lista produtos ativos de uma determinada categoria, ordenados pelo nome.
     *
     * @param categoriaId ID da categoria.
     * @return Lista de produtos ativos da categoria.
     */
    List<Produto> findByCategoriaIdAndAtivoTrueOrderByNomeAsc(Long categoriaId);

    /**
     * Busca produtos ativos cujo nome contém o termo informado (busca parcial, case-insensitive).
     *
     * @param termo Termo de busca.
     * @return Lista de produtos correspondentes.
     */
    List<Produto> findByAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(String termo);

    /**
     * Busca produtos ativos cujo nome contém o termo informado (busca parcial, case-insensitive, sem acentos).
     *
     * @param termo Termo de busca (ex: "agua" encontra "água").
     * @return Lista de produtos correspondentes.
     */
    List<Produto> findByAtivoTrueAndNomeUnaccentContainingIgnoreCaseOrderByNomeAsc(String termo);
}