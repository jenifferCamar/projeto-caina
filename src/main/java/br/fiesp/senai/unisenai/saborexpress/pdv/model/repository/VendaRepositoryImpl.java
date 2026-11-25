package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementacao do {@link VendaRepository} utilizando JPA (Hibernate).
 * <p>
 * <b>Conceito Pedagogico (Gerenciamento Explicito de Transacoes e Recursos):</b>
 * <ul>
 * <li>Cada operacao que modifica dados (DML - Insert, Update, Delete) requer a
 * demarcacao de uma transacao atomica (ACID): {@code em.getTransaction().begin()},
 * {@code commit()} e em caso de falha {@code rollback()}.</li>
 * <li>O fechamento do {@link EntityManager} em bloco {@code finally} e crucial
 * para evitar vazamento de conexoes com o banco (Connection Leak).</li>
 * <li>Injecao de Dependencias (IoC): permite receber um {@link EntityManagerFactory}
 * customizado no construtor, possibilitando testes com banco em memoria.</li>
 * </ul>
 * <p>
 * <b>Persistencia segura:</b> utiliza JPQL parametrizado (NamedQueries) em vez de
 * SQL concatenado, prevenindo SQL Injection. Transacoes garantem atomicidade
 * (commit/rollback).
 * <p>
 * <b>Padrao MVC/Repository:</b> isola a persistencia na camada de repositorio,
 * mantendo a View e o Controller desacoplados do banco.
 *
 */
public class VendaRepositoryImpl implements VendaRepository {

    private final EntityManagerFactory emf;

    public VendaRepositoryImpl() {
        this(JPAUtil.getEntityManagerFactory());
    }

    public VendaRepositoryImpl(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory nao pode ser nulo.");
    }

    @Override
    public Venda salvar(Venda venda) {
        Objects.requireNonNull(venda, "Venda nao pode ser nula.");
        EntityManager em = emf.createEntityManager(); // abre conexao com o banco
        EntityTransaction tx = em.getTransaction(); // transacao para operacao DML
        try {
            tx.begin(); // inicia transacao atomica
            em.persist(venda); // persiste a venda (e itens via cascade)
            tx.commit(); // confirma a transacao (ACID)
            return venda;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // desfaz em caso de falha (sem estado parcial)
            }
            throw new RuntimeException("Erro ao persistir venda: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close(); // evita vazamento de conexao (Connection Leak)
            }
        }
    }

    @Override
    public Venda atualizar(Venda venda) {
        Objects.requireNonNull(venda, "Venda nao pode ser nula.");
        if (venda.getId() == null) {
            throw new IllegalArgumentException("Venda a ser atualizada deve possuir um ID.");
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Venda mesclada = em.merge(venda);
            tx.commit();
            return mesclada;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao atualizar venda: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void excluir(Long id) {
        Objects.requireNonNull(id, "ID da venda nao pode ser nulo para exclusao.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Venda venda = em.find(Venda.class, id);
            if (venda == null) {
                throw new IllegalArgumentException("Venda nao encontrada para o ID: " + id);
            }
            em.remove(venda);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao excluir venda ID " + id + ": " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Venda> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        EntityManager em = emf.createEntityManager();
        try {
            // NamedQuery com JOIN FETCH para carregar os itens na mesma consulta
            // (evita LazyInitializationException apos fechar o EntityManager)
            TypedQuery<Venda> query = em.createNamedQuery("Venda.buscarPorId", Venda.class);
            query.setParameter("id", id); // parametro JPQL (sem SQL concatenado)
            return Optional.of(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty(); // venda nao encontrada
        } finally {
            if (em.isOpen()) {
                em.close(); // libera a conexao
            }
        }
    }

    @Override
    public List<Venda> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Venda> query = em.createNamedQuery("Venda.listarTodos", Venda.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Venda finalizarVenda(Venda venda) {
        Objects.requireNonNull(venda, "Venda nao pode ser nula.");
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter ao menos um item.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); // inicia transacao atomica (ACID)

            // 1. Persiste a Venda com seus itens (cascade ALL)
            em.persist(venda);

            // 2. Atualiza o estoque de cada produto vendido
            for (ItemVenda item : venda.getItens()) {
                Produto produto = em.find(Produto.class, item.getProduto().getId());
                if (produto == null) {
                    throw new IllegalArgumentException(
                            "Produto nao encontrado: " + item.getProduto().getId());
                }
                int estoqueAtual = produto.getQuantidadeEstoque();
                int quantidadeVendida = item.getQuantidade();
                if (quantidadeVendida <= 0) {
                    throw new IllegalArgumentException(
                            "Quantidade deve ser maior que zero para o produto: " + produto.getNome());
                }
                if (estoqueAtual < quantidadeVendida) {
                    throw new IllegalStateException(
                            "Estoque insuficiente para '" + produto.getNome() +
                                    "': disponivel=" + estoqueAtual + ", solicitado=" + quantidadeVendida);
                }
                // Baixa o estoque
                produto.setQuantidadeEstoque(estoqueAtual - quantidadeVendida);
                em.merge(produto); // atualiza o produto na mesma transacao
            }

            tx.commit(); // confirma tudo (venda + itens + estoque) de uma vez
            return venda;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // desfaz tudo em caso de falha (sem estado parcial)
            }
            throw new RuntimeException("Erro ao finalizar venda: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close(); // libera a conexao
            }
        }
    }
}
