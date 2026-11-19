package br.gov.sp.cps.fatec.pdv.config;

import br.gov.sp.cps.fatec.pdv.model.entity.Categoria;
import br.gov.sp.cps.fatec.pdv.model.entity.Produto;
import br.gov.sp.cps.fatec.pdv.model.repository.CategoriaRepository;
import br.gov.sp.cps.fatec.pdv.model.repository.CategoriaRepositoryImpl;
import br.gov.sp.cps.fatec.pdv.model.repository.ProdutoRepository;
import br.gov.sp.cps.fatec.pdv.model.repository.ProdutoRepositoryImpl;
import br.gov.sp.cps.fatec.pdv.model.service.ProdutoService;
import br.gov.sp.cps.fatec.pdv.util.JPAUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Carregador de Dados Iniciais (Initial Data Loader).
 * <p>
 * <b>Conceito Pedagógico (Database Seeding / Data Fixtures):</b>
 * <ul>
 * <li>Responsável por popular o banco de dados com dados iniciais necessários
 * para o funcionamento da aplicação (dados mestres, configurações, etc.).</li>
 * <li>Segue o padrão <i>CommandLineRunner</i> do Spring Boot: uma classe que
 * executa lógica de inicialização após o contexto da aplicação estar pronto.</li>
 * <li>Utiliza <b>idempotência</b>: verifica se os dados já existem antes de
 * inserir, podendo ser executada múltiplas vezes sem duplicar dados.</li>
 * <li>No contexto deste projeto (Java Swing + JPA), deve ser invocada
 * explicitamente no ponto de entrada da aplicação ({@link br.gov.sp.cps.fatec.pdv.Main}).</li>
 * </ul>
 * <p>
 * <b>Dados Iniciais - Categoria "Lanches":</b>
 * Focada em itens de lanchonete para demonstração do Cardápio.
 *
 * @author Cainã Antunes Silva
 */
public class DataLoader {

    private final ProdutoService produtoService;
    private final CategoriaRepository categoriaRepository;

    /**
     * Construtor com repositórios concretos (para uso standalone/Swing).
     */
    public DataLoader() {
        this(new ProdutoRepositoryImpl(), new CategoriaRepositoryImpl());
    }

    /**
     * Construtor com Injeção de Dependências (para testes ou Spring).
     *
     * @param produtoRepository  Repositório de produtos.
     * @param categoriaRepository Repositório de categorias.
     */
    public DataLoader(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoService = new ProdutoService(produtoRepository, categoriaRepository);
    }

    /**
     * Executa a carga inicial de dados.
     * <p>
     * Equivalente ao método {@code run(String... args)} do
     * {@code CommandLineRunner} do Spring Boot.
     * <p>
     * Cria as categorias padrão e popula produtos da categoria "Lanches".
     */
    public void run() {
        System.out.println("=== Iniciando carga de dados iniciais ===");

        // 1. Garantir categorias padrão
        Categoria lanches = garantirCategoria("Lanche", "Lanches e sanduíches artesanais");
        Categoria bebidas = garantirCategoria("Bebida", "Bebidas geladas e quentes");
        Categoria acompanhamentos = garantirCategoria("Acompanhamento", "Porções e acompanhamentos");
        Categoria sobremesas = garantirCategoria("Sobremesa", "Doces e sobremesas");

        // 2. Popular produtos da categoria "Lanches" (foco do requisito)
        popularLanches(lanches);

        // 3. Exemplos de outras categorias (opcional, para completude)
        popularOutrasCategorias(bebidas, acompanhamentos, sobremesas);

        System.out.println("=== Carga de dados iniciais concluída ===");
    }

    /**
     * Garante que uma categoria exista no banco, criando se necessário.
     * Idempotente: não duplica se já existir.
     *
     * @param nome        Nome da categoria.
     * @param descricao   Descrição da categoria.
     * @return Categoria existente ou recém-criada.
     */
    private Categoria garantirCategoria(String nome, String descricao) {
        return categoriaRepository.findByNomeIgnoreCase(nome)
                .orElseGet(() -> {
                    System.out.println("Criando categoria: " + nome);
                    Categoria nova = new Categoria(nome, descricao);
                    nova.setAtiva(true);
                    return categoriaRepository.save(nova);
                });
    }

    /**
     * Popula produtos da categoria "Lanches".
     *
     * @param categoriaLanches Categoria de lanches.
     */
    private void popularLanches(Categoria categoriaLanches) {
        List<Produto> lanches = Arrays.asList(
                criarProduto("7891234567890", "X-Burger Clássico",
                        "Hambúrguer artesanal 180g, queijo cheddar, alface, tomate, cebola roxa e molho especial no pão brioche",
                        new BigDecimal("28.90"), 50),
                criarProduto("7891234567891", "X-Bacon",
                        "Hambúrguer artesanal 180g, queijo cheddar, bacon crocante, alface, tomate e molho barbecue no pão brioche",
                        new BigDecimal("32.90"), 45),
                criarProduto("7891234567892", "X-Salada",
                        "Hambúrguer artesanal 180g, queijo mussarela, alface, tomate, cebola, picles e maionese caseira no pão brioche",
                        new BigDecimal("26.90"), 60),
                criarProduto("7891234567893", "X-Tudo",
                        "Hambúrguer artesanal 180g, queijo cheddar, bacon, ovo frito, presunto, alface, tomate, cebola e molho especial no pão brioche",
                        new BigDecimal("36.90"), 30),
                criarProduto("7891234567894", "X-Frango",
                        "Filé de frango grelhado, queijo mussarela, alface, tomate, milho verde e maionese temperada no pão brioche",
                        new BigDecimal("24.90"), 40),
                criarProduto("7891234567895", "X-Vegano",
                        "Hambúrguer de grão-de-bico e beterraba, queijo vegano, alface, tomate, cebola caramelizada e maionese vegana no pão integral",
                        new BigDecimal("29.90"), 25),
                criarProduto("7891234567896", "Duplo Cheese Bacon",
                        "Dois hambúrgueres artesanais 180g cada, queijo cheddar duplo, bacon crocante e molho barbecue no pão brioche",
                        new BigDecimal("42.90"), 20),
                criarProduto("7891234567897", "X-Calabresa",
                        "Hambúrguer artesanal 180g, queijo mussarela, calabresa fatiada grelhada, cebola roxa e molho vinagrete no pão brioche",
                        new BigDecimal("30.90"), 35)
        );

        for (Produto lanche : lanches) {
            lanche.setCategoria(categoriaLanches);
            lanche.setAtivo(true);
            salvarSeNovo(lanche);
        }
    }

    /**
     * Popula exemplos das outras categorias (opcional).
     */
    private void popularOutrasCategorias(Categoria bebidas, Categoria acompanhamentos, Categoria sobremesas) {
        // Bebidas
        List<Produto> listaBebidas = Arrays.asList(
                criarProduto("7891234567898", "Coca-Cola 350ml", "Refrigerante Coca-Cola lata 350ml gelada", new BigDecimal("6.00"), 100),
                criarProduto("7891234567899", "Guaraná Antarctica 350ml", "Refrigerante Guaraná Antarctica lata 350ml gelado", new BigDecimal("5.50"), 100),
                criarProduto("7891234567900", "Suco Natural de Laranja 300ml", "Suco 100% natural de laranja fresco", new BigDecimal("8.00"), 30),
                criarProduto("7891234567901", "Água Mineral 500ml", "Água mineral sem gás 500ml", new BigDecimal("3.00"), 200)
        );
        for (Produto p : listaBebidas) {
            p.setCategoria(bebidas);
            p.setAtivo(true);
            salvarSeNovo(p);
        }

        // Acompanhamentos
        List<Produto> listaAcompanhamentos = Arrays.asList(
                criarProduto("7891234567902", "Batata Frita Pequena", "Porção de batata frita crocante (150g)", new BigDecimal("12.00"), 50),
                criarProduto("7891234567903", "Batata Frita Grande", "Porção de batata frita crocante (300g)", new BigDecimal("18.00"), 40),
                criarProduto("7891234567904", "Onion Rings", "Anéis de cebola empanados e fritos (150g)", new BigDecimal("15.00"), 35)
        );
        for (Produto p : listaAcompanhamentos) {
            p.setCategoria(acompanhamentos);
            p.setAtivo(true);
            salvarSeNovo(p);
        }

        // Sobremesas
        List<Produto> listaSobremesas = Arrays.asList(
                criarProduto("7891234567905", "Brownie com Sorvete", "Brownie de chocolate quente com bola de sorvete de creme", new BigDecimal("14.00"), 20),
                criarProduto("7891234567906", "Petit Gateau", "Pequeno bolo de chocolate com centro cremoso e sorvete", new BigDecimal("16.00"), 15)
        );
        for (Produto p : listaSobremesas) {
            p.setCategoria(sobremesas);
            p.setAtivo(true);
            salvarSeNovo(p);
        }
    }

    /**
     * Cria um produto com os dados básicos.
     */
    private Produto criarProduto(String codigoBarras, String nome, String descricao, BigDecimal preco, Integer estoque) {
        Produto p = new Produto();
        p.setCodigoBarras(codigoBarras);
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setEstoque(estoque);
        return p;
    }

    /**
     * Salva o produto apenas se não existir (pelo código de barras).
     */
    private void salvarSeNovo(Produto produto) {
        if (!produtoService.buscarPorCodigoBarras(produto.getCodigoBarras()).isPresent()) {
            try {
                produtoService.salvar(produto);
                System.out.println("  Produto criado: " + produto.getNome() + " - R$ " + produto.getPreco());
            } catch (IllegalArgumentException e) {
                // Se falhar por unicidade de nome, tenta buscar pelo nome
                System.out.println("  Produto já existe (nome duplicado): " + produto.getNome());
            }
        } else {
            System.out.println("  Produto já existe (código de barras): " + produto.getNome());
        }
    }

    /**
     * Método principal para execução standalone (teste manual).
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Inicializa o EntityManagerFactory (JPAUtil)
        JPAUtil.getEntityManagerFactory();

        DataLoader loader = new DataLoader();
        loader.run();

        // Fecha o EntityManagerFactory ao finalizar
        JPAUtil.close();
    }
}