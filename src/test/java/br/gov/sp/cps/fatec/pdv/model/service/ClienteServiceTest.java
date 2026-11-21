package br.gov.sp.cps.fatec.pdv.model.service;

import br.gov.sp.cps.fatec.pdv.model.entity.Cliente;
import br.gov.sp.cps.fatec.pdv.model.repository.ClienteRepository;
import br.gov.sp.cps.fatec.pdv.model.repository.ClienteRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClienteServiceTest {
    private static EntityManagerFactory emf;
    private ClienteRepository repository;
    private ClienteService service;

    @BeforeAll
    static void setUpAll() { emf = Persistence.createEntityManagerFactory("pdv-test-pu"); }
    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }
    @BeforeEach
    void setUp() {
        repository = new ClienteRepositoryImpl(emf);
        service = new ClienteService(repository);
    }

    @Test
    @DisplayName("Deve salvar cliente válido via service")
    void deveSalvarClienteValido() {
        Cliente c = new Cliente("Cliente Teste", "(11) 90000-0000", null);
        Cliente salvo = service.salvar(c);
        assertNotNull(salvo.getId());
        assertEquals("Cliente Teste", salvo.getNome());
    }

    @Test
    @DisplayName("Deve rejeitar cliente sem nome")
    void deveRejeitarSemNome() {
        Cliente c = new Cliente("", "(11) 90000-0000", null);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(c));
    }

    @Test
    @DisplayName("Deve rejeitar cliente nulo")
    void deveRejeitarNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.salvar(null));
    }

    @Test
    @DisplayName("Deve atualizar cliente existente via service")
    void deveAtualizarCliente() {
        Cliente c = new Cliente("Cliente Atualizar", "(11) 91111-1111", null);
        Cliente salvo = service.salvar(c);
        salvo.setObservacoes("Obs");
        Cliente atualizado = service.atualizar(salvo);
        assertEquals("Obs", atualizado.getObservacoes());
    }

    @Test
    @DisplayName("Deve excluir cliente via service")
    void deveExcluirCliente() {
        Cliente c = new Cliente("Cliente Excluir", "(11) 92222-2222", null);
        Cliente salvo = service.salvar(c);
        service.excluir(salvo.getId());
        Optional<Cliente> apos = service.buscarPorId(salvo.getId());
        assertFalse(apos.isPresent());
    }

    @Test
    @DisplayName("Deve listar todos os clientes via service")
    void deveListarTodos() {
        service.salvar(new Cliente("Cliente A", "(11) 93333-3333", null));
        service.salvar(new Cliente("Cliente B", "(11) 94444-4444", null));
        List<Cliente> lista = service.listarTodos();
        assertFalse(lista.isEmpty());
    }
}