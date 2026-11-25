package br.fiesp.senai.unisenai.saborexpress.pdv.config;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.CategoriaRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepository;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.repository.ProdutoRepositoryImpl;
import br.fiesp.senai.unisenai.saborexpress.pdv.util.JPAUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Ponto de entrada para carga inicial de dados no Sabor Express PDV.
 * Delega a execução da carga estritamente idempotente para {@link DatabaseSeeder}.
 */
@Component
public class DataLoader {

    private final DatabaseSeeder databaseSeeder;

    /**
     * Construtor padrão com instâncias de repositório conectadas ao JPAUtil.
     */
    public DataLoader() {
        this(new ProdutoRepositoryImpl(), new CategoriaRepositoryImpl());
    }

    /**
     * Construtor com injeção de dependências para repositórios.
     *
     * @param produtoRepository   Repositório de produtos.
     * @param categoriaRepository Repositório de categorias.
     */
    public DataLoader(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this(new DatabaseSeeder(
                Objects.requireNonNull(categoriaRepository, "CategoriaRepository não pode ser nulo."),
                Objects.requireNonNull(produtoRepository, "ProdutoRepository não pode ser nulo.")
        ));
    }

    /**
     * Construtor recebendo o DatabaseSeeder diretamente.
     *
     * @param databaseSeeder Instância do DatabaseSeeder.
     */
    public DataLoader(DatabaseSeeder databaseSeeder) {
        this.databaseSeeder = Objects.requireNonNull(databaseSeeder, "DatabaseSeeder não pode ser nulo.");
    }

    /**
     * Executa a carga de dados inicial delegando ao {@link DatabaseSeeder}.
     */
    public void run() {
        databaseSeeder.seed();
    }

    /**
     * Ponto de entrada para execução standalone (teste manual).
     *
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        JPAUtil.getEntityManagerFactory();
        DataLoader loader = new DataLoader();
        loader.run();
        JPAUtil.close();
    }
}
