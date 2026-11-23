package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação do {@link ProdutoRepository} utilizando a JPA (Java Persistence
 * API) com Hibernate.
 * <p>
 * <b>Conceito Pedagógico (Gerenciamento Explícito de Transações e
 * Recursos):</b>
 * <ul>
 * <li>Cada operação que modifica dados (DML - Insert, Update, Delete) requer a
 * demarcação de uma transação
 * atômica (ACID): {@code em.getTransaction().begin()}, {@code commit()} e em
 * caso de falha {@code rollback()}.</li>
 * <li>O fechamento do {@link EntityManager} em bloco {@code finally} é crucial
 * para evitar vazamento
 * de conexões com o banco de dados (Connection Leak).</li>
 * <li>Injeção de Dependências (IoC): A classe permite receber um
 * {@link EntityManagerFactory}
 * customizado no construtor, possibilitando testes com banco em memória sem
 * alterar a lógica.</li>
 * <li>Uso da Criteria API para consultas dinâmicas e type-safe.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class ProdutoRepositoryImpl implements ProdutoRepository {

    private final EntityManagerFactory emf;

    /**
     * Construtor padrão que obtém o {@link EntityManagerFactory} singleton via
     * {@link JPAUtil}.
     */
    public ProdutoRepositoryImpl() {
        this(JPAUtil.getEntityManagerFactory());
    }

    /**
     * Construtor com Injeção de Dependência (IoC) da fábrica de conexões.
     *
     * @param emf Fábrica {@link EntityManagerFactory} a ser utilizada para criar
     *            conexões.
     */
    public ProdutoRepositoryImpl(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory não pode ser nulo.");
    }

    @Override
    public Produto save(Produto entity) {
        Objects.requireNonNull(entity, "Produto não pode ser nulo.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao persistir produto: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Produto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        EntityManager em = emf.createEntityManager();
        try {
            Produto produto = em.find(Produto.class, id);
            return Optional.ofNullable(produto);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Produto> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Produto> query = em.createNamedQuery("Produto.listarTodos", Produto.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "ID do produto não pode ser nulo para exclusão.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produto produto = em.find(Produto.class, id);
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado para o ID: " + id);
            }
            em.remove(produto);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao excluir produto ID " + id + ": " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Produto> findByCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Produto> query = em.createNamedQuery("Produto.buscarPorCodigoBarras", Produto.class);
            query.setParameter("codigoBarras", codigoBarras.trim());
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public boolean existsByCodigoBarras(String codigoBarras) {
        return findByCodigoBarras(codigoBarras).isPresent();
    }

    @Override
    public boolean existsByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Produto> root = cq.from(Produto.class);
            cq.select(cb.count(root)).where(cb.equal(cb.upper(root.get("nome")), nome.trim().toUpperCase()));
            Long count = em.createQuery(cq).getSingleResult();
            return count > 0;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public boolean existsByNomeAndIdNot(String nome, Long id) {
        if (nome == null || nome.trim().isEmpty() || id == null) {
            return false;
        }
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Produto> root = cq.from(Produto.class);
            Predicate nomePredicate = cb.equal(cb.upper(root.get("nome")), nome.trim().toUpperCase());
            Predicate idPredicate = cb.notEqual(root.get("id"), id);
            cq.select(cb.count(root)).where(cb.and(nomePredicate, idPredicate));
            Long count = em.createQuery(cq).getSingleResult();
            return count > 0;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Produto> findByAtivoTrueOrderByNomeAsc() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Produto> query = em.createNamedQuery("Produto.listarAtivos", Produto.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Produto> findByCategoriaIdAndAtivoTrueOrderByNomeAsc(Long categoriaId) {
        if (categoriaId == null) {
            return List.of();
        }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Produto> query = em.createNamedQuery("Produto.listarPorCategoria", Produto.class);
            query.setParameter("categoriaId", categoriaId);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Produto> findByAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return findByAtivoTrueOrderByNomeAsc();
        }
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Produto> cq = cb.createQuery(Produto.class);
            Root<Produto> root = cq.from(Produto.class);

            Predicate ativoPredicate = cb.isTrue(root.get("ativo"));
            Predicate nomePredicate = cb.like(cb.upper(root.get("nome")), "%" + termo.trim().toUpperCase() + "%");

            cq.where(cb.and(ativoPredicate, nomePredicate));
            cq.orderBy(cb.asc(root.get("nome")));

            return em.createQuery(cq).getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    // ==========================================
    // MÉTODOS LEGADOS (Compatibilidade)
    // ==========================================

    /**
     * @deprecated Use {@link #save(Object)} instead.
     */
    @Deprecated
    public Produto salvar(Produto produto) {
        return save(produto);
    }

    /**
     * @deprecated Use {@link #save(Object)} instead.
     */
    @Deprecated
    public Produto atualizar(Produto produto) {
        return save(produto);
    }

    /**
     * @deprecated Use {@link #deleteById(Long)} instead.
     */
    @Deprecated
    public void excluir(Long id) {
        deleteById(id);
    }

    /**
     * @deprecated Use {@link #findById(Long)} instead.
     */
    @Deprecated
    public Optional<Produto> buscarPorId(Long id) {
        return findById(id);
    }

    /**
     * @deprecated Use {@link #findByCodigoBarras(String)} instead.
     */
    @Deprecated
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return findByCodigoBarras(codigoBarras);
    }

    /**
     * @deprecated Use {@link #findAll()} instead.
     */
    @Deprecated
    public List<Produto> listarTodos() {
        return findAll();
    }
}