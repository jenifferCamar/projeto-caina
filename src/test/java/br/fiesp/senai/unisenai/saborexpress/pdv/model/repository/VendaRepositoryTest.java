package br.fiesp.senai.unisenai.saborexpress.pdv.model.repository;

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

    @BeforeAll
    static void setUpAll() { emf = Persistence.createEntityManagerFactory("pdv-test-pu"); }

    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }

    @BeforeEach
    void setUp() {
        repository = new VendaRepositoryImpl(emf);
        produtoRepository = new ProdutoRepositoryImpl(emf);
    }

    @Test
    @DisplayName("Deve salvar venda com itens e gerar ID")
    void deveSalvarVendaComItens() {
        Produto p = produtoRepository.save(new Produto("789", "Lanche", new BigDecimal("15.00"), 10));
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 2, new BigDecimal("15.00")));
        Venda salva = repository.salvar(v);
        assertNotNull(salva.getId());
        assertEquals(1, salva.getItens().size());
    }

    @Test
    @DisplayName("Deve buscar venda por ID com itens")
    void deveBuscarPorId() {
        Produto p = produtoRepository.save(new Produto("790", "Bebida", new BigDecimal("5.00"), 10));
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
        Produto p = produtoRepository.save(new Produto("791", "Doce", new BigDecimal("3.00"), 10));
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 1, new BigDecimal("3.00")));
        repository.salvar(v);
        List<Venda> lista = repository.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    @DisplayName("Deve finalizar venda de forma atomica")
    void deveFinalizarVendaAtomica() {
        Produto p = produtoRepository.save(new Produto("792", "Suco", new BigDecimal("8.00"), 5));
        Venda v = new Venda();
        v.adicionarItem(new ItemVenda(v, p, 2, new BigDecimal("8.00")));
        Venda finalizada = repository.finalizarVenda(v);
        assertNotNull(finalizada.getId());
        Produto atualizado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(3, atualizado.getQuantidadeEstoque());
    }
}
