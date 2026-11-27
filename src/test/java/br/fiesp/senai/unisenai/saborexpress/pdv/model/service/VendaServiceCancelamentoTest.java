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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class VendaServiceCancelamentoTest {

    private static EntityManagerFactory emf;
    private ProdutoService produtoService;
    private VendaService vendaService;

    @BeforeAll
    static void setUpAll() { emf = Persistence.createEntityManagerFactory("pdv-test-pu"); }

    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }

    @BeforeEach
    void setUp() {
        ProdutoRepository produtoRepository = new ProdutoRepositoryImpl(emf);
        VendaRepository vendaRepository = new VendaRepositoryImpl(emf);
        CategoriaRepository categoriaRepository = new CategoriaRepositoryImpl(emf);
        produtoService = new ProdutoService(produtoRepository, categoriaRepository);
        vendaService = new VendaService(vendaRepository, produtoService);
        Categoria categoria = categoriaRepository.findAll().stream().findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria("Cat-" + System.nanoTime(), "Teste")));
        Produto produto = new Produto("P-" + System.nanoTime(), "Produto-" + System.nanoTime(), new BigDecimal("10.00"), 10);
        produto.setCategoria(categoria);
        produtoService.salvar(produto);
        this.produtoId = produto.getId();
    }

    private Long produtoId;

    private Venda criarVendaFinalizada(int qtd) {
        Produto produto = produtoService.buscarPorId(produtoId).orElseThrow();
        Venda venda = new Venda(null, "Cliente");
        venda.setStatus(Venda.STATUS_FINALIZADA);
        venda.adicionarItem(new ItemVenda(venda, produto, qtd, produto.getPreco()));
        venda.recalcularTotal();
        return new VendaRepositoryImpl(emf).finalizarVenda(venda);
    }

    @Test
    @DisplayName("Cancela venda finalizada e estorna estoque atomicamente")
    void deveCancelarVendaEEstornarEstoque() {
        Venda salva = criarVendaFinalizada(3);
        int estoqueAposVenda = produtoService.buscarPorId(produtoId).orElseThrow().getEstoque();
        Venda cancelada = vendaService.cancelarVenda(salva.getId());
        assertEquals(Venda.STATUS_CANCELADA, cancelada.getStatus());
        assertEquals(estoqueAposVenda + 3, produtoService.buscarPorId(produtoId).orElseThrow().getEstoque());
    }

    @Test
    @DisplayName("Não permite cancelar venda já cancelada")
    void naoDeveCancelarVendaJaCancelada() {
        Venda salva = criarVendaFinalizada(1);
        vendaService.cancelarVenda(salva.getId());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.cancelarVenda(salva.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("cancel"));
    }

    @Test
    @DisplayName("Notifica observadores ao cancelar venda")
    void deveNotificarObservadoresAoCancelar() throws InterruptedException {
        Venda salva = criarVendaFinalizada(1);
        AtomicBoolean notificado = new AtomicBoolean(false);
        vendaService.addObserver(vendas -> notificado.set(true));
        vendaService.cancelarVenda(salva.getId());
        Thread.sleep(300);
        assertTrue(notificado.get());
    }
}
