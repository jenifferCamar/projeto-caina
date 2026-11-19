package br.gov.sp.cps.fatec.pdv.model.service;

import br.gov.sp.cps.fatec.pdv.model.entity.Categoria;
import br.gov.sp.cps.fatec.pdv.model.entity.Produto;
import br.gov.sp.cps.fatec.pdv.model.observer.ProdutoObserver;
import br.gov.sp.cps.fatec.pdv.model.repository.CategoriaRepository;
import br.gov.sp.cps.fatec.pdv.model.repository.CategoriaRepositoryImpl;
import br.gov.sp.cps.fatec.pdv.model.repository.ProdutoRepository;

import javax.swing.SwingUtilities;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Camada de Serviço de Negócio (Service Layer / Subject no Padrão Observer).
 * <p>
 * <b>Conceito Pedagógico (Regras de Negócio, IoC, Bean Validation e Thread-Safety):</b>
 * <ul>
 * <li><b>Validação de Domínio:</b> Aplica validações estritas exigidas pelas
 * regras de negócio
 * (código de barras único e preenchido, nome obrigatório e único, preço estritamente
 * positivo,
 * estoque não-negativo, categoria obrigatória).</li>
 * <li><b>Bean Validation (JSR 380):</b> Utiliza {@link Validator} para validar
 * anotações {@link jakarta.validation.constraints}
 * nas entidades antes da persistência.</li>
 * <li><b>Inversão de Dependência (DIP):</b> Depende exclusivamente das abstrações
 * {@link ProdutoRepository} e {@link CategoriaRepository}.</li>
 * <li><b>Padrão Observer (Subject):</b> Mantém a lista de ouvintes registrados
 * e dispara
 * notificações reativas após mutações de dados bem-sucedidas.</li>
 * <li><b>Thread Safety com Swing (EDT):</b> Assegura que o método
 * {@link ProdutoObserver#onProdutosChanged(List)}
 * seja sempre despachado na <i>Event Dispatch Thread</i> do Swing via
 * {@link SwingUtilities#invokeLater(Runnable)},
 * evitando condições de corrida (Race Conditions) e travamentos de UI.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final Validator validator;
    private final List<ProdutoObserver> observers;

    /**
     * Construtor com Injeção de Dependências das interfaces de repositório.
     *
     * @param produtoRepository  Abstração do repositório de produtos.
     * @param categoriaRepository Abstração do repositório de categorias.
     */
    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = Objects.requireNonNull(produtoRepository, "ProdutoRepository não pode ser nulo.");
        this.categoriaRepository = Objects.requireNonNull(categoriaRepository, "CategoriaRepository não pode ser nulo.");
        this.observers = new ArrayList<>();

        // Inicializa o Validator do Bean Validation (Hibernate Validator)
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Construtor de compatibilidade para código legado que usa apenas ProdutoRepository.
     * Cria internamente uma instância padrão de CategoriaRepositoryImpl.
     *
     * @param produtoRepository Abstração do repositório de produtos.
     * @deprecated Use {@link #ProdutoService(ProdutoRepository, CategoriaRepository)} instead.
     */
    @Deprecated
    public ProdutoService(ProdutoRepository produtoRepository) {
        this(produtoRepository, new CategoriaRepositoryImpl());
    }

    // ==========================================
    // GERENCIAMENTO DE OBSERVERS (GOF PATTERN)
    // ==========================================

    /**
     * Registra um novo observador para receber notificações de mudanças de dados.
     *
     * @param observer Implementação de {@link ProdutoObserver}.
     */
    public synchronized void addObserver(ProdutoObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Remove um observador previamente registrado.
     *
     * @param observer Observador a ser removido.
     */
    public synchronized void removeObserver(ProdutoObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica todos os observadores cadastrados com a lista de produtos
     * atualizada.
     * Garante o disparo seguro na Event Dispatch Thread (EDT) do Swing.
     */
    public void notificarObservadores() {
        List<Produto> listaAtualizada = produtoRepository.findAll();
        List<ProdutoObserver> copiaObservers;
        synchronized (this) {
            copiaObservers = new ArrayList<>(observers);
        }

        // Despacho seguro na Thread de Eventos do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            for (ProdutoObserver observer : copiaObservers) {
                try {
                    observer.onProdutosChanged(Collections.unmodifiableList(listaAtualizada));
                } catch (Exception e) {
                    System.err.println("Erro ao notificar observador: " + e.getMessage());
                }
            }
        });
    }

    // ==========================================
    // REGRAS DE NEGÓCIO E PERSISTÊNCIA - PRODUTOS
    // ==========================================

    /**
     * Cadastra um novo produto após validação completa de negócio e Bean Validation.
     *
     * @param produto Entidade a ser cadastrada.
     * @return Produto salvo com ID atribuído.
     * @throws IllegalArgumentException se violar regras de negócio ou validações.
     */
    public Produto salvar(Produto produto) {
        validarRegrasProduto(produto);

        // Validação de Unicidade do Código de Barras
        if (produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
            throw new IllegalArgumentException(
                    "Já existe um produto cadastrado com o código de barras informado: " + produto.getCodigoBarras());
        }

        // Validação de Unicidade do Nome
        if (produtoRepository.existsByNome(produto.getNome())) {
            throw new IllegalArgumentException(
                    "Já existe um produto cadastrado com o nome informado: " + produto.getNome());
        }

        // Validação de existência da Categoria
        validarCategoria(produto.getCategoria());

        Produto salvo = produtoRepository.save(produto);
        notificarObservadores();
        return salvo;
    }

    /**
     * Atualiza um produto existente após validações de negócio.
     *
     * @param produto Entidade contendo as alterações.
     * @return Produto atualizado.
     * @throws IllegalArgumentException se violar regras de negócio ou validações.
     */
    public Produto atualizar(Produto produto) {
        if (produto.getId() == null) {
            throw new IllegalArgumentException("O produto deve possuir um ID para ser atualizado.");
        }
        validarRegrasProduto(produto);

        // Validação de Código de Barras duplicado para outro produto diferente
        Optional<Produto> existenteCodigoBarras = produtoRepository.findByCodigoBarras(produto.getCodigoBarras());
        if (existenteCodigoBarras.isPresent() && !existenteCodigoBarras.get().getId().equals(produto.getId())) {
            throw new IllegalArgumentException(
                    "O código de barras '" + produto.getCodigoBarras() +
                            "' já pertence a outro produto cadastrado (#" + existenteCodigoBarras.get().getId() + " - "
                            + existenteCodigoBarras.get().getNome() + ").");
        }

        // Validação de Nome duplicado para outro produto diferente
        if (produtoRepository.existsByNomeAndIdNot(produto.getNome(), produto.getId())) {
            throw new IllegalArgumentException(
                    "O nome '" + produto.getNome() +
                            "' já pertence a outro produto cadastrado.");
        }

        // Validação de existência da Categoria
        validarCategoria(produto.getCategoria());

        Produto atualizado = produtoRepository.save(produto);
        notificarObservadores();
        return atualizado;
    }

    /**
     * Exclui um produto pelo seu identificador e notifica os observadores.
     *
     * @param id Identificador do produto a ser removido.
     * @throws IllegalArgumentException se ID for nulo.
     */
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }
        produtoRepository.deleteById(id);
        notificarObservadores();
    }

    /**
     * Busca um produto pelo ID.
     *
     * @param id Identificador.
     * @return {@link Optional} contendo o produto.
     */
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Busca um produto pelo código de barras.
     *
     * @param codigoBarras Código de barras.
     * @return {@link Optional} contendo o produto.
     */
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Retorna a listagem atual de todos os produtos cadastrados.
     *
     * @return Lista de produtos.
     */
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    /**
     * Retorna a listagem de produtos ativos (visíveis no cardápio), ordenados por nome.
     *
     * @return Lista de produtos ativos.
     */
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrueOrderByNomeAsc();
    }

    /**
     * Retorna a listagem de produtos ativos de uma categoria específica.
     *
     * @param categoriaId ID da categoria.
     * @return Lista de produtos ativos da categoria.
     */
    public List<Produto> listarAtivosPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaIdAndAtivoTrueOrderByNomeAsc(categoriaId);
    }

    /**
     * Busca produtos ativos por termo no nome (busca parcial, case-insensitive).
     *
     * @param termo Termo de busca.
     * @return Lista de produtos correspondentes.
     */
    public List<Produto> buscarPorNome(String termo) {
        return produtoRepository.findByAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(termo);
    }

    // ==========================================
    // REGRAS DE NEGÓCIO - CATEGORIAS
    // ==========================================

    /**
     * Cadastra uma nova categoria.
     *
     * @param categoria Categoria a ser cadastrada.
     * @return Categoria salva.
     */
    public Categoria salvarCategoria(Categoria categoria) {
        validarCategoriaRegras(categoria);
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com o nome: " + categoria.getNome());
        }
        return categoriaRepository.save(categoria);
    }

    /**
     * Lista todas as categorias ativas.
     *
     * @return Lista de categorias ativas.
     */
    public List<Categoria> listarCategoriasAtivas() {
        return categoriaRepository.findByAtivaTrueOrderByNomeAsc();
    }

    /**
     * Busca uma categoria por ID.
     *
     * @param id ID da categoria.
     * @return {@link Optional} contendo a categoria.
     */
    public Optional<Categoria> buscarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Busca uma categoria por nome (case-insensitive).
     *
     * @param nome Nome da categoria.
     * @return {@link Optional} contendo a categoria.
     */
    public Optional<Categoria> buscarCategoriaPorNome(String nome) {
        return categoriaRepository.findByNomeIgnoreCase(nome);
    }

    // ==========================================
    // VALIDAÇÕES INTERNAS DO DOMÍNIO
    // ==========================================

    /**
     * Valida as regras de negócio do produto, incluindo Bean Validation.
     *
     * @param produto Produto a ser validado.
     * @throws IllegalArgumentException se houver violações.
     */
    private void validarRegrasProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Dados do produto não podem ser nulos.");
        }

        // 1. Bean Validation (anotações na entidade)
        Set<ConstraintViolation<Produto>> violations = validator.validate(produto);
        if (!violations.isEmpty()) {
            StringBuilder mensagens = new StringBuilder("Validação falhou: ");
            for (ConstraintViolation<Produto> v : violations) {
                mensagens.append(v.getMessage()).append("; ");
            }
            throw new IllegalArgumentException(mensagens.toString());
        }

        // 2. Validações adicionais de negócio não cobertas por anotações
        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().trim().isEmpty()) {
            throw new IllegalArgumentException("O Código de Barras é obrigatório.");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O Nome do Produto é obrigatório.");
        }

        if (produto.getPreco() == null) {
            throw new IllegalArgumentException("O Preço do Produto é obrigatório.");
        }

        if (produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O Preço do Produto deve ser estritamente positivo (maior que zero).");
        }

        if (produto.getCategoria() == null) {
            throw new IllegalArgumentException("A Categoria é obrigatória.");
        }

        if (produto.getAtivo() == null) {
            throw new IllegalArgumentException("O status Ativo/Inativo é obrigatório.");
        }

        if (produto.getEstoque() == null) {
            throw new IllegalArgumentException("A Quantidade em Estoque é obrigatória.");
        }

        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("A Quantidade em Estoque não pode ser negativa.");
        }
    }

    /**
     * Valida se a categoria existe e está ativa.
     *
     * @param categoria Categoria a ser validada.
     * @throws IllegalArgumentException se a categoria for inválida.
     */
    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("A Categoria é obrigatória.");
        }
        if (categoria.getId() == null) {
            throw new IllegalArgumentException("A Categoria deve possuir um ID válido.");
        }
        Optional<Categoria> existente = categoriaRepository.findById(categoria.getId());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada: ID " + categoria.getId());
        }
        if (!existente.get().getAtiva()) {
            throw new IllegalArgumentException("A categoria '" + existente.get().getNome() + "' está inativa e não pode ser usada.");
        }
    }

    /**
     * Valida as regras de negócio da categoria (Bean Validation + regras básicas).
     *
     * @param categoria Categoria a ser validada.
     * @throws IllegalArgumentException se houver violações.
     */
    private void validarCategoriaRegras(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Dados da categoria não podem ser nulos.");
        }

        Set<ConstraintViolation<Categoria>> violations = validator.validate(categoria);
        if (!violations.isEmpty()) {
            StringBuilder mensagens = new StringBuilder("Validação de categoria falhou: ");
            for (ConstraintViolation<Categoria> v : violations) {
                mensagens.append(v.getMessage()).append("; ");
            }
            throw new IllegalArgumentException(mensagens.toString());
        }

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O Nome da Categoria é obrigatório.");
        }
    }
}