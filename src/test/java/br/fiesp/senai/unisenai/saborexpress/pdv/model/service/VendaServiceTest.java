package br.fiesp.senai.unisenai.saborexpress.pdv.model.service;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.BusinessException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.ValidationException;
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

import static org.junit.jupiter.api.Assertions.*;

class VendaServiceTest {
    private static EntityManagerFactory emf;
    private VendaService service;
    private ProdutoRepository produtoRepository;
    private ProdutoService produtoService;

    @BeforeAll
    static void setUpAll() { emf = Persistence.createEntityManagerFactory("pdv-test-pu"); }

    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }

    @BeforeEach
    void setUp() {
        VendaRepository vendaRepository = new VendaRepositoryImpl(emf);
        produtoRepository = new ProdutoRepositoryImpl(emf);
        produtoService = new ProdutoService(produtoRepository);
        service = new VendaService(vendaRepository, produtoService);
    }

    @Test
    @DisplayName("Deve finalizar venda com estoque suficiente e baixar estoque")
    void deveFinalizarVendaComEstoqueSuficiente() {
        Produto p = produtoRepository.save(new Produto("800", "X-Burger", new BigDecimal("20.00"), 5));
        List<ItemCarrinho> carrinho = new ArrayList<>();
        carrinho.add(new ItemCarrinho(p.getId(), p.getNome(), 2, p.getPreco()));
        Venda venda = service.finalizarVenda(carrinho, null, null);
        assertNotNull(venda.getId());
        Produto atualizado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(3, atualizado.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void deveLancarExcecaoEstoqueInsuficiente() {
        Produto p = produtoRepository.save(new Produto("801", "Refrigerante", new BigDecimal("8.00"), 1));
        List<ItemCarrinho> carrinho = new ArrayList<>();
        carrinho.add(new ItemCarrinho(p.getId(), p.getNome(), 5, p.getPreco()));
        assertThrows(BusinessException.class, () -> service.finalizarVenda(carrinho, null, null));
        Produto inalterado = produtoRepository.findById(p.getId()).orElseThrow();
        assertEquals(1, inalterado.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando carrinho vazio")
    void deveLancarExcecaoCarrinhoVazio() {
        assertThrows(ValidationException.class, () -> service.finalizarVenda(new ArrayList<>(), null, null));
    }
}
