package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.VendaRepositoryImpl;
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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class VendaServiceCancelamentoTest {

    private static EntityManagerFactory emf;
    private ProdutoRepository produtoRepository;
    private VendaRepository vendaRepository;
    private ProdutoService produtoService;
    private VendaService vendaService;
    private Categoria categoria;

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
        produtoRepository = new ProdutoRepositoryImpl(emf);
        vendaRepository = new VendaRepositoryImpl(emf);
        CategoriaRepository categoriaRepository = new CategoriaRepositoryImpl(emf);
        produtoService = new ProdutoService(produtoRepository, categoriaRepository);
        vendaService = new VendaService(vendaRepository, produtoService);
        categoria = categoriaRepository.findAll().stream().findFirst().orElseGet(() -> {
            Categoria c = new Categoria("Cat-" + System.nanoTime(), "Teste");
            return categoriaRepository.save(c);
        });

    }

    private Produto novoProduto(String codigo, String nome, String preco, int estoque) {
        Produto p = new Produto(codigo, nome, new BigDecimal(preco), estoque);
        p.setCategoria(categoria);
        return produtoService.salvar(p);
    }

    @Test
    @DisplayName("Cancela venda finalizada e estorna estoque atomicamente")
    void deveCancelarVendaEEstornarEstoque() {
        Produto produto = novoProduto("C1-" + System.nanoTime(), "Prod Cancel", "10.00", 10);

        Venda venda = new Venda(null, "Cliente Teste");
        venda.setStatus(Venda.STATUS_FINALIZADA);
        venda.adicionarItem(new ItemVenda(venda, produto, 3, produto.getPreco()));
        venda.recalcularTotal();
        Venda salva = vendaRepository.finalizarVenda(venda);

        Produto aposVenda = produtoService.buscarPorId(produto.getId()).orElseThrow();
        assertEquals(7, aposVenda.getEstoque());

        Venda cancelada = vendaService.cancelarVenda(salva.getId());

        assertEquals(Venda.STATUS_CANCELADA, cancelada.getStatus());
        Produto aposCancelamento = produtoService.buscarPorId(produto.getId()).orElseThrow();
        assertEquals(10, aposCancelamento.getEstoque());
    }

    @Test
    @DisplayName("Não permite cancelar venda já cancelada")
    void naoDeveCancelarVendaJaCancelada() {
        Produto produto = novoProduto("C2-" + System.nanoTime(), "Prod Cancel 2", "5.00", 5);
        Venda venda = new Venda(null, "Cliente");
        venda.setStatus(Venda.STATUS_FINALIZADA);
        venda.adicionarItem(new ItemVenda(venda, produto, 1, produto.getPreco()));
        venda.recalcularTotal();
        Venda salva = vendaRepository.finalizarVenda(venda);

        vendaService.cancelarVenda(salva.getId());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.cancelarVenda(salva.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("cancel"));
        Produto apos = produtoService.buscarPorId(produto.getId()).orElseThrow();
        assertEquals(5, apos.getEstoque());
    }

    @Test
    @DisplayName("Notifica observadores ao cancelar venda")
    void deveNotificarObservadoresAoCancelar() throws InterruptedException {
        Produto produto = novoProduto("C3-" + System.nanoTime(), "Prod Obs", "8.00", 4);
        Venda venda = new Venda(null, "Cliente");
        venda.setStatus(Venda.STATUS_FINALIZADA);
        venda.adicionarItem(new ItemVenda(venda, produto, 2, produto.getPreco()));
        venda.recalcularTotal();
        Venda salva = vendaRepository.finalizarVenda(venda);

        AtomicBoolean notificado = new AtomicBoolean(false);
        vendaService.addObserver(vendas -> notificado.set(true));

        vendaService.cancelarVenda(salva.getId());
        Thread.sleep(300);

        assertTrue(notificado.get(), "Observer de venda deve ser notificado no cancelamento");
    }
}
