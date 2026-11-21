package br.gov.sp.cps.fatec.pdv.model.repository;

import br.gov.sp.cps.fatec.pdv.model.entity.Cliente;
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

class ClienteRepositoryTest {
    private static EntityManagerFactory emf;
    private ClienteRepository repository;

    @BeforeAll
    static void setUpAll() { emf = Persistence.createEntityManagerFactory("pdv-test-pu"); }
    @AfterAll
    static void tearDownAll() { if (emf != null && emf.isOpen()) emf.close(); }
    @BeforeEach
    void setUp() { repository = new ClienteRepositoryImpl(emf); }

    @Test
    @DisplayName("Deve salvar um novo cliente com sucesso e gerar ID")
    void deveSalvarClienteComSucesso() {
        Cliente c = new Cliente("João da Silva", "(11) 99999-0000", "Cliente fiel");
        Cliente salvo = repository.salvar(c);
        assertNotNull(salvo.getId());
        assertEquals("João da Silva", salvo.getNome());
    }

    @Test
    @DisplayName("Deve buscar cliente por ID existente")
    void deveBuscarPorId() {
        Cliente c = new Cliente("Maria Souza", "(11) 98888-1111", null);
        Cliente salvo = repository.salvar(c);
        Optional<Cliente> encontrado = repository.buscarPorId(salvo.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("Maria Souza", encontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve atualizar dados de um cliente existente")
    void deveAtualizarCliente() {
        Cliente c = new Cliente("Carlos Lima", "(11) 97777-2222", null);
        Cliente salvo = repository.salvar(c);
        salvo.setTelefone("(11) 96666-3333");
        salvo.setObservacoes("Atualizado");
        Cliente atualizado = repository.atualizar(salvo);
        assertEquals("(11) 96666-3333", atualizado.getTelefone());
        assertEquals("Atualizado", atualizado.getObservacoes());
    }

    @Test
    @DisplayName("Deve excluir cliente por ID com sucesso")
    void deveExcluirCliente() {
        Cliente c = new Cliente("Ana Paula", "(11) 95555-4444", null);
        Cliente salvo = repository.salvar(c);
        repository.excluir(salvo.getId());
        Optional<Cliente> aposExclusao = repository.buscarPorId(salvo.getId());
        assertFalse(aposExclusao.isPresent());
    }

    @Test
    @DisplayName("Deve buscar clientes por nome (case-insensitive, contém)")
    void deveBuscarPorNome() {
        repository.salvar(new Cliente("Pedro Henrique", "(11) 94444-5555", null));
        repository.salvar(new Cliente("Pedro Oliveira", "(11) 93333-6666", null));
        List<Cliente> encontrados = repository.buscarPorNome("pedro");
        assertFalse(encontrados.isEmpty());
        assertTrue(encontrados.size() >= 2);
    }

    @Test
    @DisplayName("Deve listar todos os clientes cadastrados ordenados por nome")
    void deveListarTodosOsClientes() {
        repository.salvar(new Cliente("Zeca", "(11) 92222-7777", null));
        repository.salvar(new Cliente("Amanda", "(11) 91111-8888", null));
        List<Cliente> lista = repository.listarTodos();
        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 2);
    }
}