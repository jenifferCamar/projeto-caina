package br.fiesp.senai.unisenai.saborexpress.pdv.config;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Componente responsável pelo povoamento inicial de dados (Database Seeder)
 * do cardápio demonstrativo do Sabor Express PDV.
 * <p>
 * Executa a carga apenas se a base de dados estiver vazia (idempotência estrita),
 * garantindo que inicializações subsequentes não dupliquem registros ou violem
 * constraints de chave primária/unicidade.
 */
@Component
public class DatabaseSeeder {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;

    public DatabaseSeeder(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) {
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository não pode ser nulo.");
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository não pode ser nulo.");
    }

    /**
     * Listener para inicialização automática caso gerenciado por contexto Spring.
     *
     * @param event Evento disparado quando o contexto da aplicação é inicializado ou atualizado.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        seed();
    }

    /**
     * Executa a carga inicial condicional do cardápio.
     * Se já existirem categorias ou produtos, a operação é ignorada para preservar os dados.
     */
    @Transactional
    public void seed() {
        // Idempotência estrita: verifica se já existem dados cadastrados
        if (!categoriaRepository.findAll().isEmpty() || !produtoRepository.findAll().isEmpty()) {
            System.out.println("[DatabaseSeeder] Base de dados já contém registros. Seed ignorado para evitar duplicidade.");
            return;
        }

        System.out.println("[DatabaseSeeder] Base de dados vazia. Iniciando carga inicial do cardápio...");

        // 1. Criação das Categorias Obrigatórias
        Categoria lanches = new Categoria("Lanche", "Lanches, hambúrgueres e sanduíches artesanais");
        lanches.setAtiva(true);
        lanches = categoriaRepository.save(lanches);

        Categoria bebidas = new Categoria("Bebida", "Bebidas geladas, refrigerantes, sucos naturais e água mineral");
        bebidas.setAtiva(true);
        bebidas = categoriaRepository.save(bebidas);

        Categoria acompanhamentos = new Categoria("Acompanhamento", "Porções e acompanhamentos crocantes");
        acompanhamentos.setAtiva(true);
        acompanhamentos = categoriaRepository.save(acompanhamentos);

        Categoria sobremesas = new Categoria("Sobremesa", "Doces e sobremesas artesanais");
        sobremesas.setAtiva(true);
        sobremesas = categoriaRepository.save(sobremesas);

        // 2. Criação dos Produtos com Estoque e Preços Reais
        List<Produto> produtos = List.of(
                // --- Lanches ---
                new Produto("7891234567890", "X-Burger Clássico",
                        "Hambúrguer artesanal 180g, queijo cheddar derretido, alface fresca, tomate, cebola roxa e molho especial no pão brioche",
                        new BigDecimal("28.90"), lanches, 50),
                new Produto("7891234567891", "X-Bacon Artesanal",
                        "Hambúrguer artesanal 180g, queijo cheddar duplo, fatias crocantes de bacon, alface, tomate e molho barbecue no pão brioche",
                        new BigDecimal("32.90"), lanches, 50),
                new Produto("7891234567892", "X-Salada Especial",
                        "Hambúrguer artesanal 180g, queijo mussarela, alface americana, tomate fresco, picles artesanal e maionese caseira no pão brioche",
                        new BigDecimal("26.90"), lanches, 50),

                // --- Bebidas ---
                new Produto("7891234567898", "Coca-Cola Lata",
                        "Refrigerante Coca-Cola original lata 350ml gelada",
                        new BigDecimal("6.00"), bebidas, 50),
                new Produto("7891234567900", "Suco de Laranja Natural",
                        "Suco 100% natural de laranja fresco espremido na hora (copo 300ml)",
                        new BigDecimal("8.50"), bebidas, 50),
                new Produto("7891234567901", "Água Mineral",
                        "Água mineral natural sem gás garrafa 500ml gelada",
                        new BigDecimal("4.00"), bebidas, 50),

                // --- Acompanhamentos ---
                new Produto("7891234567902", "Batata Frita Média",
                        "Porção média de batatas fritas crocantes e douradas com sal e páprica (200g)",
                        new BigDecimal("16.00"), acompanhamentos, 50),
                new Produto("7891234567904", "Anéis de Cebola Crocantes",
                        "Porção de anéis de cebola empanados crocantes servidos com molho da casa (150g)",
                        new BigDecimal("18.00"), acompanhamentos, 50),

                // --- Sobremesas ---
                new Produto("7891234567907", "Pudim de Leite",
                        "Fatia generosa de tradicional pudim de leite condensado cremoso com calda de caramelo artesanal",
                        new BigDecimal("12.00"), sobremesas, 50),
                new Produto("7891234567905", "Brownie com Sorvete",
                        "Brownie artesanal de chocolate meio amargo servido com uma bola de sorvete de baunilha e calda quente",
                        new BigDecimal("19.00"), sobremesas, 50)
        );

        for (Produto produto : produtos) {
            produto.setAtivo(true);
            produtoRepository.save(produto);
        }

        System.out.println(String.format("[DatabaseSeeder] Carga inicial concluída: 4 categorias e %d produtos persistidos.", produtos.size()));
    }
}
