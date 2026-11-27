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
import java.time.LocalDateTime;
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

    @Test
    @DisplayName("Deve cancelar venda finalizada e estornar estoque de forma atomica")
    void deveCancelarVendaEEstornarEstoque() {
        Produto p = novoProduto("795", "Refrigerante", new BigDecimal("8.00"), 10);
        Venda v = new Venda();
        v.setStatus(Venda.STATUS_FINALIZADA);
        v.adicionarItem(new ItemVenda(v, p, 3, new BigDecimal("8.00")));

        // Finaliza: estoque cai de 10 para 7
        Venda finalizada = repository.finalizarVenda(v);
        assertEquals(Venda.STATUS_FINALIZADA, finalizada.getStatus());
        assertEquals(7, produtoRepository.findById(p.getId()).orElseThrow().getQuantidadeEstoque());

        // Cancela: estoque volta de 7 para 10
        Venda cancelada = repository.cancelarVenda(finalizada.getId());
        assertEquals(Venda.STATUS_CANCELADA, cancelada.getStatus());
        assertEquals(10, produtoRepository.findById(p.getId()).orElseThrow().getQuantidadeEstoque());
    }

    // ==========================================
    // TESTES DE METRICAS DE VENDAS
    // ==========================================

    private Venda vendaFinalizadaComTotal(String codigoBarras, String nome, BigDecimal preco, Integer quantidade, Integer estoqueInicial, LocalDateTime dataHora) {
        Produto p = novoProduto(codigoBarras, nome, preco, estoqueInicial);
        Venda v = new Venda();
        v.setDataHora(dataHora);
        v.setStatus(Venda.STATUS_FINALIZADA);
        v.adicionarItem(new ItemVenda(v, p, quantidade, preco));
        return repository.finalizarVenda(v);
    }

    @Test
    @DisplayName("Deve contar vendas finalizadas sem filtro de periodo")
    void deveContarVendasFinalizadasSemFiltro() {
        LocalDateTime ontem = LocalDateTime.now().minusDays(1);
        LocalDateTime hoje = LocalDateTime.now();
        vendaFinalizadaComTotal("810", "Venda Ontem", new BigDecimal("20.00"), 1, 10, ontem);
        vendaFinalizadaComTotal("811", "Venda Hoje", new BigDecimal("30.00"), 1, 10, hoje);

        Long total = repository.contarVendasFinalizadas();
        assertTrue(total >= 2);
    }

    @Test
    @DisplayName("Deve contar vendas finalizadas com filtro de periodo")
    void deveContarVendasFinalizadasComFiltroPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        vendaFinalizadaComTotal("812", "Venda no Periodo", new BigDecimal("15.00"), 1, 10, inicio.plusHours(1));

        Long total = repository.contarVendasFinalizadas(inicio, fim);
        assertTrue(total >= 1);
    }

    @Test
    @DisplayName("Deve somar faturamento finalizado sem filtro de periodo")
    void deveSomarFaturamentoFinalizadoSemFiltro() {
        vendaFinalizadaComTotal("813", "Faturamento A", new BigDecimal("10.00"), 2, 10, LocalDateTime.now());
        vendaFinalizadaComTotal("814", "Faturamento B", new BigDecimal("20.00"), 1, 10, LocalDateTime.now());

        BigDecimal total = repository.somarFaturamentoFinalizado();
        assertTrue(total.compareTo(BigDecimal.valueOf(40.00)) >= 0);
    }

    @Test
    @DisplayName("Deve somar faturamento finalizado com filtro de periodo")
    void deveSomarFaturamentoFinalizadoComFiltroPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(1);
        vendaFinalizadaComTotal("815", "Faturamento no Periodo", new BigDecimal("50.00"), 1, 10, inicio.plusHours(1));

        BigDecimal total = repository.somarFaturamentoFinalizado(inicio, fim);
        assertTrue(total.compareTo(BigDecimal.valueOf(50.00)) >= 0);
    }
}