package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VendaRepositoryTest {
    private static EntityManagerFactory emf;
    private VendaRepository repository;
    private ProdutoRepository produtoRepository;
    private CategoriaRepository categoriaRepository;
    private Categoria categoria;

    @BeforeAll
    static void setUpAll() {
        emf = Persistence.createEntityManagerFactory("pdv-test-pu");
        // Cria uma categoria compartilhada por todos os testes (H2 em memoria e compartilhado)
        EntityManagerFactory sharedEmf = emf;
        CategoriaRepository catRepo = new CategoriaRepositoryImpl(sharedEmf);
        Optional<Categoria> existente = catRepo.findAll().stream().findFirst();
        sharedCategoria = existente.orElseGet(() -> catRepo.save(new Categoria("Lanches", "Categoria de lanches")));
    }

    private static Categoria sharedCategoria;

    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }

    @BeforeEach
    void setUp() {
        repository = new VendaRepositoryImpl(emf);
        produtoRepository = new ProdutoRepositoryImpl(emf);
        categoriaRepository = new CategoriaRepositoryImpl(emf);
        categoria = sharedCategoria;
    }

    private Produto novoProduto(String codigoBarras, String nome, BigDecimal preco, Integer estoque) {
        Produto p = new Produto(codigoBarras, nome, "", preco, categoria, estoque, 0);
        return produtoRepository.save(p);
    }

    @Test
    @DisplayName("Deve salvar venda com itens e gerar ID")
    void deveSalvarVendaComItens() {
        Produto p = novoProduto("789", "Lanche", new BigDecimal("15.00"), 10);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 2, new BigDecimal("15.00")));
        Venda salva = repository.salvar(v);
        assertNotNull(salva.getId());
        assertEquals(1, salva.getItens().size());
    }

    @Test
    @DisplayName("Deve buscar venda por ID com itens")
    void deveBuscarPorId() {
        Produto p = novoProduto("790", "Bebida", new BigDecimal("5.00"), 10);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 1, new BigDecimal("5.00")));
        Venda salva = repository.salvar(v);
        Optional<Venda> encontrado = repository.buscarPorId(salva.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(1, encontrado.get().getItens().size());
    }

    @Test
    @DisplayName("Deve listar todas as vendas")
    void deveListarTodos() {
        Produto p = novoProduto("791", "Doce", new BigDecimal("3.00"), 10);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 1, new BigDecimal("3.00")));
        repository.salvar(v);
        List<Venda> lista = repository.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    @DisplayName("Deve finalizar venda de forma atomica")
    void deveFinalizarVendaAtomica() {
        Produto p = novoProduto("792", "Suco", new BigDecimal("8.00"), 5);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 2, new BigDecimal("8.00")));
        Venda finalizada = repository.finalizarVenda(v);
        assertNotNull(finalizada.getId());
        Produto atualizado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(3, atualizado.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando venda nao existe")
    void deveRetornarVazioQuandoVendaNaoExiste() {
        Optional<Venda> encontrado = repository.buscarPorId(99999L);
        assertTrue(encontrado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar status e total de uma venda existente")
    void deveAtualizarVendaExistente() {
        Produto p = novoProduto("793", "Salgado", new BigDecimal("6.00"), 10);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 1, new BigDecimal("6.00")));
        Venda salva = repository.salvar(v);

        salva.setStatus(Venda.STATUS_FINALIZADA);
        salva.setTotal(new BigDecimal("12.00"));
        Venda atualizada = repository.atualizar(salva);

        assertEquals(Venda.STATUS_FINALIZADA, atualizada.getStatus());
        assertEquals(new BigDecimal("12.00"), atualizada.getTotal());
    }

    @Test
    @DisplayName("Deve excluir venda por ID com sucesso")
    void deveExcluirVenda() {
        Produto p = novoProduto("794", "Bolo", new BigDecimal("25.00"), 5);
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 1, new BigDecimal("25.00")));
        Venda salva = repository.salvar(v);

        repository.excluir(salva.getId());
        Optional<Venda> aposExclusao = repository.buscarPorId(salva.getId());
        assertTrue(aposExclusao.isEmpty());
    }
}