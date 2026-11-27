package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.MetricasVendasDTO;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.BusinessException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.PersistenceException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ValidationException;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(venda);
            tx.commit();
            return venda;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // desfaz em caso de falha (sem estado parcial)
            }
            throw new PersistenceException("Falha ao persistir venda.", e);

        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Venda atualizar(Venda venda) {
        Objects.requireNonNull(venda, "Venda nao pode ser nula.");
        if (venda.getId() == null) {
            throw new ValidationException("Venda a ser atualizada deve possuir um ID.");
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
            throw new PersistenceException("Falha ao atualizar venda.", e);

        } finally {
            if (em.isOpen()) em.close();
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
                throw new BusinessException("Venda não encontrada para o ID: " + id);
            }
            em.remove(venda);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenceException("Falha ao excluir venda.", e);

        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Optional<Venda> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Venda> query = em.createNamedQuery("Venda.buscarPorId", Venda.class);
            query.setParameter("id", id);
            return Optional.of(query.getSingleResult());
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public List<Venda> listarTodos() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Venda> query = em.createNamedQuery("Venda.listarTodos", Venda.class);
            return query.getResultList();
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Venda finalizarVenda(Venda venda) {
        Objects.requireNonNull(venda, "Venda nao pode ser nula.");
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new ValidationException("A venda deve conter ao menos um item.");
        }

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(venda);
            for (ItemVenda item : venda.getItens()) {
                Produto produto = em.find(Produto.class, item.getProduto().getId());
                if (produto == null) {
                    throw new BusinessException("Produto não encontrado: " + item.getProduto().getId());

                }
                int estoqueAtual = produto.getQuantidadeEstoque();
                int quantidadeVendida = item.getQuantidade();
                if (quantidadeVendida <= 0) {
                    throw new ValidationException("Quantidade deve ser maior que zero para o produto: " + produto.getNome());
                }
                if (estoqueAtual < quantidadeVendida) {
                    throw new BusinessException("Estoque insuficiente para '" + produto.getNome() + "': disponível=" + estoqueAtual + ", solicitado=" + quantidadeVendida);

                }
                produto.setQuantidadeEstoque(estoqueAtual - quantidadeVendida);
                em.merge(produto);
            }
            tx.commit();
            return venda;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // desfaz tudo em caso de falha (sem estado parcial)
            }
            throw new PersistenceException("Falha ao finalizar venda.", e);

        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public Venda cancelarVenda(Long id) {
        Objects.requireNonNull(id, "ID da venda nao pode ser nulo para cancelamento.");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Venda venda = em.find(Venda.class, id);
            if (venda == null) {
                throw new IllegalArgumentException("Venda nao encontrada para o ID: " + id);
            }
            if (Venda.STATUS_CANCELADA.equals(venda.getStatus())) {
                throw new IllegalStateException("A venda #" + id + " ja esta cancelada.");
            }
            if (!Venda.STATUS_FINALIZADA.equals(venda.getStatus())) {
                throw new IllegalStateException("Apenas vendas finalizadas podem ser canceladas. Status atual: " + venda.getStatus());
            }
            for (ItemVenda item : venda.getItens()) {
                Produto produto = em.find(Produto.class, item.getProduto().getId());
                if (produto == null) {
                    throw new IllegalArgumentException("Produto nao encontrado para estorno: " + item.getProduto().getId());
                }
                int estoqueAtual = produto.getQuantidadeEstoque() != null ? produto.getQuantidadeEstoque() : 0;
                int quantidade = item.getQuantidade() != null ? item.getQuantidade() : 0;
                produto.setQuantidadeEstoque(estoqueAtual + quantidade);
                em.merge(produto);
            }
            venda.setStatus(Venda.STATUS_CANCELADA);
            Venda atualizada = em.merge(venda);
            tx.commit();
            return atualizada;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao cancelar venda: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    // ==========================================
    // CONSULTAS AGREGADAS DE METRICAS
    // ==========================================

    @Override
    public Long contarVendasFinalizadas() {
        return contarVendasFinalizadas(null, null);
    }

    @Override
    public Long contarVendasFinalizadas(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = emf.createEntityManager();
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(v) FROM Venda v WHERE v.status = :status");
            if (inicio != null && fim != null) {
                sql.append(" AND v.dataHora BETWEEN :inicio AND :fim");
            } else if (inicio != null) {
                sql.append(" AND v.dataHora >= :inicio");
            } else if (fim != null) {
                sql.append(" AND v.dataHora <= :fim");
            }
            TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
            query.setParameter("status", Venda.STATUS_FINALIZADA);
            if (inicio != null) {
                query.setParameter("inicio", inicio);
            }
            if (fim != null) {
                query.setParameter("fim", fim);
            }
            return query.getSingleResult();
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    @Override
    public BigDecimal somarFaturamentoFinalizado() {
        return somarFaturamentoFinalizado(null, null);
    }

    @Override
    public BigDecimal somarFaturamentoFinalizado(LocalDateTime inicio, LocalDateTime fim) {
        EntityManager em = emf.createEntityManager();
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT SUM(v.total) FROM Venda v WHERE v.status = :status AND v.total IS NOT NULL");
            if (inicio != null && fim != null) {
                sql.append(" AND v.dataHora BETWEEN :inicio AND :fim");
            } else if (inicio != null) {
                sql.append(" AND v.dataHora >= :inicio");
            } else if (fim != null) {
                sql.append(" AND v.dataHora <= :fim");
            }
            TypedQuery<BigDecimal> query = em.createQuery(sql.toString(), BigDecimal.class);
            query.setParameter("status", Venda.STATUS_FINALIZADA);
            if (inicio != null) {
                query.setParameter("inicio", inicio);
            }
            if (fim != null) {
                query.setParameter("fim", fim);
            }
            BigDecimal resultado = query.getSingleResult();
            return resultado != null ? resultado : BigDecimal.ZERO;
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}
