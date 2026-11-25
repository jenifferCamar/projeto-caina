package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação do {@link ClienteRepository} utilizando JPA/Hibernate.
 *
 */
public class ClienteRepositoryImpl implements ClienteRepository {

    private final EntityManagerFactory emf;

    public ClienteRepositoryImpl() {
        this(JPAUtil.getEntityManagerFactory());
    }

    public ClienteRepositoryImpl(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory não pode ser nulo.");
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cliente);
            tx.commit();
            return cliente;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao persistir cliente: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Cliente atualizar(Cliente cliente) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo.");
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente a ser atualizado deve possuir um ID.");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cliente mesclado = em.merge(cliente);
            tx.commit();
            return mesclado;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public void excluir(Long id) {
        Objects.requireNonNull(id, "ID do cliente não pode ser nulo para exclusão.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente == null) {
                throw new IllegalArgumentException("Cliente não encontrado para o ID: " + id);
            }
            em.remove(cliente);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao excluir cliente ID " + id + ": " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(Cliente.class, id));
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public List<Cliente> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return List.of();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Cliente> query = em.createNamedQuery("Cliente.buscarPorNome", Cliente.class);
            query.setParameter("nome", nome.trim());
            return query.getResultList();
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public List<Cliente> buscarPorTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) return List.of();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Cliente> query = em.createQuery(
                    "SELECT c FROM Cliente c WHERE c.telefone LIKE :tel ORDER BY c.nome ASC", Cliente.class);
            query.setParameter("tel", "%" + telefone.trim() + "%");
            return query.getResultList();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Cliente> query = em.createNamedQuery("Cliente.listarTodos", Cliente.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}