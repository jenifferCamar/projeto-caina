package br.fiesp.senai.unisenai.saborexpress.pdv.config;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseSeederTest {

    private static EntityManagerFactory emf;
    private CategoriaRepository categoriaRepository;
    private ProdutoRepository produtoRepository;
    private DatabaseSeeder seeder;

    @BeforeAll
    static void setUpAll() {
        emf = Persistence.createEntityManagerFactory("pdv-test-pu");
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        categoriaRepository = new CategoriaRepositoryImpl(emf);
        produtoRepository = new ProdutoRepositoryImpl(emf);
        seeder = new DatabaseSeeder(categoriaRepository, produtoRepository);

        // Limpa as tabelas antes de cada teste para garantir isolamento
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM ItemVenda").executeUpdate();
        em.createQuery("DELETE FROM Venda").executeUpdate();
        em.createQuery("DELETE FROM Produto").executeUpdate();
        em.createQuery("DELETE FROM Categoria").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Deve popular cardápio completo com 4 categorias e 10 produtos quando base estiver vazia")
    void devePopularBaseVaziaComSucesso() {
        assertTrue(categoriaRepository.findAll().isEmpty(), "Categorias devem estar vazias inicialmente");
        assertTrue(produtoRepository.findAll().isEmpty(), "Produtos devem estar vazios inicialmente");

        seeder.seed();

        List<Categoria> categorias = categoriaRepository.findAll();
        List<Produto> produtos = produtoRepository.findAll();

        assertEquals(4, categorias.size(), "Devem ser criadas exatamente 4 categorias");
        assertEquals(10, produtos.size(), "Devem ser criados exatamente 10 produtos");

        // Valida categorias esperadas
        assertTrue(categoriaRepository.findByNomeIgnoreCase("Lanche").isPresent());
        assertTrue(categoriaRepository.findByNomeIgnoreCase("Bebida").isPresent());
        assertTrue(categoriaRepository.findByNomeIgnoreCase("Acompanhamento").isPresent());
        assertTrue(categoriaRepository.findByNomeIgnoreCase("Sobremesa").isPresent());

        // Valida regras de negócio dos produtos
        for (Produto p : produtos) {
            assertNotNull(p.getId(), "Produto deve possuir ID gerado");
            assertNotNull(p.getNome(), "Produto deve possuir nome");
            assertNotNull(p.getCodigoBarras(), "Produto deve possuir código de barras");
            assertNotNull(p.getDescricao(), "Produto deve possuir descrição");
            assertTrue(p.getPreco().compareTo(BigDecimal.ZERO) > 0, "Preço deve ser maior que zero");
            assertEquals(50, p.getEstoque(), "Estoque inicial deve ser 50 unidades");
            assertTrue(p.getAtivo(), "Produto deve estar ativo");
            assertNotNull(p.getCategoria(), "Produto deve ter categoria associada");
        }
    }

    @Test
    @DisplayName("Deve manter idempotência estrita e ignorar seed se base já contiver registros")
    void deveIgnorarSeedQuandoBaseJaContiverDados() {
        // Executa primeira carga
        seeder.seed();
        assertEquals(4, categoriaRepository.findAll().size());
        assertEquals(10, produtoRepository.findAll().size());

        // Executa segunda carga (deve ignorar e não duplicar nada)
        seeder.seed();
        assertEquals(4, categoriaRepository.findAll().size(), "Não deve duplicar categorias na segunda execução");
        assertEquals(10, produtoRepository.findAll().size(), "Não deve duplicar produtos na segunda execução");
    }

    @Test
    @DisplayName("DataLoader deve delegar corretamente para o DatabaseSeeder")
    void dataLoaderDeveExecutarSeedCorretamente() {
        DataLoader dataLoader = new DataLoader(seeder);
        dataLoader.run();

        assertEquals(4, categoriaRepository.findAll().size());
        assertEquals(10, produtoRepository.findAll().size());
    }
}
