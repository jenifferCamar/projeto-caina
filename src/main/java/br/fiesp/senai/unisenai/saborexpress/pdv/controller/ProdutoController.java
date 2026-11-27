package br.fiesp.senai.unisenai.saborexpress.pdv.controller;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Categoria;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.BusinessException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.exception.PersistenceException;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.ProdutoObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.ProdutoService;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.MainFrame;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.ProdutoFormView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.ProdutoListView;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controlador Principal do Módulo de Gestão de Produtos (Padrão MVC &
 * Mediator).
 * <p>
 * <b>Conceito Pedagógico (Padrão Controller & Inversão de Dependência):</b>
 * <ul>
 * <li><b>Orquestrador Central:</b> O Controller captura as interações do
 * usuário disparadas
 * nas telas Swing (eventos de botões), valida e converte dados de entrada e
 * aciona a camada de negócio.</li>
 * <li><b>Isolamento Completo:</b> A View nunca se comunica diretamente com o
 * Repositório ou banco de dados.</li>
 * <li><b>Inversão de Dependência (IoC):</b> O Controller recebe suas
 * dependências no construtor através
 * de interfaces e classes de serviço, desacoplando a camada de controle da
 * instanciação física.</li>
 * <li><b>Integração com o Padrão Observer:</b> O Controller implementa
 * {@link ProdutoObserver},
 * sendo notificado de forma reativa a cada persistência para atualizar a JTable
 * da tela.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class ProdutoController implements ProdutoObserver {

    private final MainFrame mainFrame;
    private final ProdutoListView listView;
    private final ProdutoFormView formView;
    private final ProdutoService service;

    /**
     * Construtor recebendo a camada de serviço.
     *
     * @param mainFrame Janela principal da aplicação.
     * @param service   Camada de serviços e regras de negócio.
     */
    public ProdutoController(MainFrame mainFrame, ProdutoService service) {
        this.mainFrame = Objects.requireNonNull(mainFrame, "MainFrame não pode ser nulo.");
        this.listView = mainFrame.getListView();
        this.formView = mainFrame.getFormView();
        this.service = Objects.requireNonNull(service, "ProdutoService não pode ser nulo.");

        // Timer para debounce da busca (1.5s)
        this.searchDebounceTimer = new Timer(1500, e -> realizarBuscaComDebounce());
        this.searchDebounceTimer.setRepeats(false);

        vincularEventos();
    }

    /**
     * Inicializa o fluxo do controlador, conectando os observadores e carregando a
     * listagem inicial.
     */
    public void inicializar() {
        this.service.addObserver(this);
        this.service.notificarObservadores();
        this.mainFrame.mostrarLista();
    }

    /**
     * Configura os ouvintes de eventos (Listeners) nos componentes visuais das
     * telas.
     */
    private void vincularEventos() {
        // --- Eventos da Tela de Listagem ---
        listView.getBtnNovo().addActionListener(e -> onNovoProduto());
        listView.getBtnEditar().addActionListener(e -> onEditarProduto());
        listView.getBtnExcluir().addActionListener(e -> onExcluirProduto());
        listView.getBtnRecarregar().addActionListener(e -> onRecarregarProdutos());
        // Botão Buscar removido - busca agora é feita via debounce no campo de texto
        listView.getBtnLimparFiltro().addActionListener(e -> onLimparFiltro());
        listView.getBtnRecarregar().addActionListener(e -> onRecarregarProdutos());

        // Configurar debounce na busca (document listener)
        configurarDebounceBusca();

        // --- Eventos da Tela de Formulário ---
        formView.getBtnSalvar().addActionListener(e -> onSalvarProduto());
        formView.getBtnLimpar().addActionListener(e -> onLimparFormulario());
        formView.getBtnVoltar().addActionListener(e -> onVoltarParaListagem());
    }

    /**
     * Configura o debounce (1.5s) no campo de busca.
     * A busca é realizada automaticamente após 1.5s de inatividade do usuário.
     */
    private void configurarDebounceBusca() {
        listView.getTxtBusca().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                reiniciarTimerBusca();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                reiniciarTimerBusca();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reiniciarTimerBusca();
            }

            private void reiniciarTimerBusca() {
                searchDebounceTimer.restart();
            }
        });
    }

    /**
     * Realiza a busca com debounce (chamado pelo timer após 1.5s de inatividade).
     * Usa busca sem acento para encontrar "agua" e "água" igualmente.
     */
    private void realizarBuscaComDebounce() {
        String termo = listView.getBuscaTexto();
        if (termo == null || termo.isEmpty()) {
            service.notificarObservadores();
            return;
        }
        listView.atualizarTabela(service.buscarPorNomeSemAcento(termo));
    }

    // ==========================================
    // TRATADORES DE EVENTOS (HANDLERS)
    // ==========================================

    private void onNovoProduto() {
        formView.configurarModoNovo();
        mainFrame.mostrarFormulario();
    }

    private void onVoltarParaListagem() {
        mainFrame.mostrarLista();
    }

    private void onLimparFormulario() {
        formView.limparCampos();
    }

    private void onRecarregarProdutos() {
        service.notificarObservadores();
    }

    private void onLimparFiltro() {
        listView.limparBusca();
        service.notificarObservadores();
    }

    private void onEditarProduto() {
        Produto selecionado = listView.getProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Selecione um produto na tabela para editar.",
                    "Atenção - Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        formView.carregarParaEdicao(selecionado);
        mainFrame.mostrarFormulario();
    }

    private void onExcluirProduto() {
        Produto selecionado = listView.getProdutoSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Selecione um produto na tabela para excluir.",
                    "Atenção - Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                mainFrame,
                String.format("Deseja realmente excluir o produto abaixo?\n\nID: #%d\nNome: %s\nCódigo: %s",
                        selecionado.getId(), selecionado.getNome(), selecionado.getCodigoBarras()),
                "Confirmar Exclusão de Produto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                service.excluir(selecionado.getId());
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Produto excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Erro ao excluir produto: " + ex.getMessage(),
                        "Erro de Persistência",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSalvarProduto() {
        try {
            String codigoBarras = formView.getCodigoBarras();
            String nome = formView.getNome();
            String precoStr = formView.getPrecoTexto().replace(",", ".");
            String estoqueStr = formView.getQuantidadeEstoqueTexto();

            // Validação de formato de números
            BigDecimal preco;
            try {
                preco = new BigDecimal(precoStr);
            } catch (NumberFormatException ex) {
                throw new BusinessException("Preço inválido. Informe um valor numérico válido (ex.: 19.99).");
            }

            Integer quantidadeEstoque;
            try {
                quantidadeEstoque = Integer.parseInt(estoqueStr);
            } catch (NumberFormatException ex) {
                throw new BusinessException("Quantidade em estoque inválida. Informe um número inteiro (ex.: 10).");
            }

            Integer estoqueMinimo;
            try {
                estoqueMinimo = Integer.parseInt(formView.getEstoqueMinimoTexto());
            } catch (NumberFormatException ex) {
                throw new BusinessException("Estoque mínimo inválido. Informe um número inteiro (ex.: 3).");
            }

            Long id = formView.getProdutoIdAtual();
            boolean ativo = formView.isAtivoSelecionado();

            if (id == null) {
                // Modo Cadastro (Novo)
                Categoria categoriaPadrao = service.listarCategoriasAtivas().stream()
                        .findFirst()
                        .orElse(null);

                Produto novoProduto = new Produto(codigoBarras, nome, "", preco, categoriaPadrao, ativo, quantidadeEstoque, estoqueMinimo);
                service.salvar(novoProduto);
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Produto cadastrado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Modo Atualização (Edição)
                Produto existente = service.buscarPorId(id).orElse(null);
                Categoria categoria = (existente != null) ? existente.getCategoria() : null;
                String descricao = (existente != null) ? existente.getDescricao() : "";

                Produto produtoAtualizado = new Produto(id, codigoBarras, nome, descricao, preco, categoria, ativo, quantidadeEstoque, estoqueMinimo);
                service.atualizar(produtoAtualizado);
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Produto atualizado com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Retorna à tela de listagem
            mainFrame.mostrarLista();

        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    ex.getMessage(),
                    "Validação de Dados",
                    JOptionPane.WARNING_MESSAGE);
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Não foi possível salvar os dados. Tente novamente.",
                    "Erro de Persistência",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Erro de persistência ao salvar produto: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Ocorreu um erro inesperado ao salvar o produto.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Erro inesperado ao salvar produto: " + ex.getMessage());
        }
    }

    // ==========================================
    // IMPLEMENTAÇÃO DO OBSERVER PATTERN
    // ==========================================

    @Override
    public void onProdutosChanged(List<Produto> produtos) {
        // Atualiza a tabela na View quando o Modelo/Service sinaliza alterações
        listView.atualizarTabela(produtos);
    }
}
