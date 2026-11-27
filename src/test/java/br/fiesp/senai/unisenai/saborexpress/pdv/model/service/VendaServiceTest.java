package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.BusinessException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ProdutoInativoException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ValidationException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VendaServiceTest {
    private static EntityManagerFactory emf;
    private VendaService service;
    private ProdutoRepository produtoRepository;
    private ProdutoService produtoService;
    private CategoriaRepository categoriaRepository;
    private Categoria categoria;

    @BeforeAll
    static void setUpAll() {
        emf = Persistence.createEntityManagerFactory("pdv-test-pu");
        CategoriaRepository catRepo = new CategoriaRepositoryImpl(emf);
        Optional<Categoria> existente = catRepo.findAll().stream().findFirst();
        sharedCategoria = existente.orElseGet(() -> catRepo.save(new Categoria("Lanches", "Categoria de lanches")));
    }

    private static Categoria sharedCategoria;

    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }

    @BeforeEach
    void setUp() {
        produtoRepository = new ProdutoRepositoryImpl(emf);
        categoriaRepository = new CategoriaRepositoryImpl(emf);
        categoria = sharedCategoria;
        VendaRepository vendaRepository = new VendaRepositoryImpl(emf);
        produtoService = new ProdutoService(produtoRepository, categoriaRepository);
        service = new VendaService(vendaRepository, produtoService);
    }

    private Produto novoProduto(String codigoBarras, String nome, BigDecimal preco, Integer estoque, boolean ativo) {
        Produto p = new Produto(codigoBarras, nome, "", preco, categoria, ativo, estoque, 0);
        return produtoService.salvar(p);
    }

    private List<ItemCarrinho> carrinhoDe(Produto produto, Integer quantidade) {
        List<ItemCarrinho> carrinho = new ArrayList<>();
        carrinho.add(new ItemCarrinho(produto.getId(), produto.getNome(), quantidade, produto.getPreco()));
        return carrinho;
    }

    @Test
    @DisplayName("Deve finalizar venda com estoque suficiente e baixar estoque")
    void deveFinalizarVendaComEstoqueSuficiente() {
        Produto p = novoProduto("800", "X-Burger", new BigDecimal("20.00"), 5, true);
        Venda venda = service.finalizarVenda(carrinhoDe(p, 2), null, null);
        assertNotNull(venda.getId());
        Produto atualizado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(3, atualizado.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void deveLancarExcecaoEstoqueInsuficiente() {
        Produto p = novoProduto("801", "Refrigerante", new BigDecimal("8.00"), 1, true);
        assertThrows(BusinessException.class, () -> service.finalizarVenda(carrinhoDe(p, 5), null, null));
        Produto inalterado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(1, inalterado.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando carrinho vazio")
    void deveLancarExcecaoCarrinhoVazio() {
        assertThrows(ValidationException.class, () -> service.finalizarVenda(new ArrayList<>(), null, null));
    }

    @Test
    @DisplayName("Deve finalizar venda com cliente associado")
    void deveFinalizarVendaComCliente() {
        Produto p = novoProduto("802", "X-Salada", new BigDecimal("18.00"), 5, true);
        Venda venda = service.finalizarVenda(carrinhoDe(p, 1), 42L, "Maria Silva");
        assertNotNull(venda.getId());
        assertEquals(42L, venda.getClienteId());
        assertEquals("Maria Silva", venda.getClienteNome());
    }

    @Test
    @DisplayName("Deve finalizar venda com multiplos produtos no carrinho")
    void deveFinalizarVendaComMultiplosProdutos() {
        Produto p1 = novoProduto("803", "Hamburguer", new BigDecimal("20.00"), 5, true);
        Produto p2 = novoProduto("804", "Batata Frita", new BigDecimal("10.00"), 10, true);
        List<ItemCarrinho> carrinho = new ArrayList<>();
        carrinho.add(new ItemCarrinho(p1.getId(), p1.getNome(), 1, p1.getPreco()));
        carrinho.add(new ItemCarrinho(p2.getId(), p2.getNome(), 2, p2.getPreco()));
        Venda venda = service.finalizarVenda(carrinho, null, null);
        assertNotNull(venda.getId());
        assertEquals(2, venda.getItens().size());
        Produto estoqueP1 = produtoRepository.findById(p1.getId()).orElseThrow();
        Produto estoqueP2 = produtoRepository.findById(p2.getId()).orElseThrow();
        assertEquals(4, estoqueP1.getEstoque());
        assertEquals(8, estoqueP2.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar excecao quando produto nao encontrado")
    void deveLancarExcecaoProdutoNaoEncontrado() {
        List<ItemCarrinho> carrinho = new ArrayList<>();
        carrinho.add(new ItemCarrinho(99999L, "Produto Inexistente", 1, new BigDecimal("10.00")));
        assertThrows(BusinessException.class, () -> service.finalizarVenda(carrinho, null, null));
    }

    @Test
    @DisplayName("Deve lançar excecao quando produto esta inativo")
    void deveLancarExcecaoProdutoInativo() {
        Produto p = novoProduto("805", "Produto Inativo", new BigDecimal("15.00"), 5, false);
        assertThrows(ProdutoInativoException.class, () -> service.finalizarVenda(carrinhoDe(p, 1), null, null));
    }

    @Test
    @DisplayName("Deve buscar venda por ID apos finalizar")
    void deveBuscarVendaPorIdAposFinalizar() {
        Produto p = novoProduto("806", "X-Bacon", new BigDecimal("22.00"), 5, true);
        Venda venda = service.finalizarVenda(carrinhoDe(p, 1), null, null);
        Optional<Venda> encontrado = service.buscarPorId(venda.getId());
        assertTrue(encontrado.isPresent());
        assertEquals(venda.getId(), encontrado.get().getId());
    }

    @Test
    @DisplayName("Deve listar todas as vendas finalizadas")
    void deveListarTodasAsVendas() {
        Produto p1 = novoProduto("807", "Item A", new BigDecimal("10.00"), 5, true);
        Produto p2 = novoProduto("808", "Item B", new BigDecimal("10.00"), 5, true);
        service.finalizarVenda(carrinhoDe(p1, 1), null, null);
        service.finalizarVenda(carrinhoDe(p2, 1), null, null);
        List<Venda> vendas = service.listarTodos();
        assertFalse(vendas.isEmpty());
        assertTrue(vendas.size() >= 2);
    }
}