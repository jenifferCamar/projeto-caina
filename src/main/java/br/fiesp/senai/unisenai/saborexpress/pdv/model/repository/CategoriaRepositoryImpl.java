package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
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
 * Implementação do {@link CategoriaRepository} utilizando a JPA (Java Persistence
 * API) com Hibernate.
 *
 * @author Cainã Antunes Silva
 */
public class CategoriaRepositoryImpl implements CategoriaRepository {

    private final EntityManagerFactory emf;

    /**
     * Construtor padrão que obtém o {@link EntityManagerFactory} singleton via
     * {@link JPAUtil}.
     */
    public CategoriaRepositoryImpl() {
        this(JPAUtil.getEntityManagerFactory());
    }

    /**
     * Construtor com Injeção de Dependência (IoC) da fábrica de conexões.
     *
     * @param emf Fábrica {@link EntityManagerFactory} a ser utilizada para criar
     *            conexões.
     */
    public CategoriaRepositoryImpl(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory não pode ser nulo.");
    }

    @Override
    public Categoria save(Categoria entity) {
        Objects.requireNonNull(entity, "Categoria não pode ser nula.");
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
            throw new RuntimeException("Erro ao persistir categoria: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        EntityManager em = emf.createEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, id);
            return Optional.ofNullable(categoria);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public List<Categoria> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
            Root<Categoria> root = cq.from(Categoria.class);
            cq.orderBy(cb.asc(root.get("nome")));
            return em.createQuery(cq).getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, "ID da categoria não pode ser nulo para exclusão.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria == null) {
                throw new IllegalArgumentException("Categoria não encontrada para o ID: " + id);
            }
            em.remove(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao excluir categoria ID " + id + ": " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Categoria> findByNomeIgnoreCase(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return Optional.empty();
        }
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
            Root<Categoria> root = cq.from(Categoria.class);
            cq.where(cb.equal(cb.upper(root.get("nome")), nome.trim().toUpperCase()));
            TypedQuery<Categoria> query = em.createQuery(cq);
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
    public boolean existsByNomeIgnoreCase(String nome) {
        return findByNomeIgnoreCase(nome).isPresent();
    }

    @Override
    public List<Categoria> findByAtivaTrueOrderByNomeAsc() {
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Categoria> cq = cb.createQuery(Categoria.class);
            Root<Categoria> root = cq.from(Categoria.class);
            cq.where(cb.isTrue(root.get("ativa")));
            cq.orderBy(cb.asc(root.get("nome")));
            return em.createQuery(cq).getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}