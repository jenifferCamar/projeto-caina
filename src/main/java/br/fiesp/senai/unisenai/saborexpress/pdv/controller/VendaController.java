package br.fiesp.senai.unisenai.saborexpress.pdv.controller;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemCarrinho;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Produto;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.ProdutoObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.VendaObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.ProdutoService;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.VendaService;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.CaixaView;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.MainFrame;
import br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel.VendaTableModel;

import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Controlador Principal do Módulo de Caixa (Padrão MVC & Mediator).
 * <p>
 * <b>Conceito Pedagógico (Padrão Controller & Inversão de Dependência):</b>
 * <ul>
 * <li><b>Orquestrador Central:</b> O Controller captura as interações do usuário
 * disparadas na tela de Caixa (botões de adicionar, remover, finalizar,
 * cancelar), valida e converte dados de entrada e aciona a camada de negócio
 * ({@link VendaService} e {@link ProdutoService}).</li>
 * <li><b>Isolamento Completo:</b> A View ({@link CaixaView}) nunca se comunica
 * diretamente com o Repositório ou banco de dados. Toda a lógica de negócio,
 * validação de estoque e persistência fica estritamente nos Services.</li>
 * <li><b>Inversão de Dependência (IoC):</b> O Controller recebe suas
 * dependências no construtor através de classes de serviço, desacoplando a
 * camada de controle da instanciação física.</li>
 * <li><b>Integração com o Padrão Observer:</b> O Controller implementa
 * {@link VendaObserver}, sendo notificado de forma reativa a cada venda
 * finalizada para atualizar o {@link VendaTableModel} do histórico de vendas.</li>
 * </ul>
 */
public class VendaController implements ProdutoObserver, VendaObserver {

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat
            .getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private final MainFrame mainFrame;
    private final CaixaView caixaView;
    private final VendaTableModel vendaTableModel;
    private final ProdutoService produtoService;
    private final VendaService vendaService;

    /**
     * Construtor recebendo a janela principal, a view de caixa e os serviços.
     *
     * @param mainFrame    Janela principal da aplicação.
     * @param caixaView    Visão do caixa de vendas.
     * @param produtoService Camada de serviços de produtos.
     * @param vendaService  Camada de serviços de vendas.
     */
    public VendaController(MainFrame mainFrame, CaixaView caixaView,
                           ProdutoService produtoService, VendaService vendaService) {
        this.mainFrame = Objects.requireNonNull(mainFrame, "MainFrame não pode ser nulo.");
        this.caixaView = Objects.requireNonNull(caixaView, "CaixaView não pode ser nula.");
        this.produtoService = Objects.requireNonNull(produtoService, "ProdutoService não pode ser nulo.");
        this.vendaService = Objects.requireNonNull(vendaService, "VendaService não pode ser nulo.");
        this.vendaTableModel = new VendaTableModel();

        // Registra o Controller como observador de Produtos para atualizar o caixa reativamente
        this.produtoService.addObserver(this);

        vincularEventos();
    }

    /**
     * Inicializa o fluxo do controlador, conectando os observadores, carregando
     * o cardápio de produtos ativos e exibindo a tela de caixa.
     */
    public void inicializar() {
        this.vendaService.addObserver(this);
        this.vendaService.notificarObservadores();

        // Carrega produtos ativos do cardápio na view
        caixaView.atualizarProdutos(produtoService.listarAtivos());

        this.mainFrame.mostrarCaixa();
    }

    /**
     * Configura os ouvintes de eventos (Listeners) nos componentes visuais da
     * tela de Caixa.
     */
    private void vincularEventos() {
        // --- Eventos do Carrinho ---
        caixaView.getBtnAdicionarCarrinho().addActionListener(e -> onAdicionarAoCarrinho());
        caixaView.getBtnRemoverCarrinho().addActionListener(e -> onRemoverDoCarrinho());
        caixaView.getBtnLimparCarrinho().addActionListener(e -> onLimparCarrinho());

        // --- Eventos de Finalização ---
        caixaView.getBtnFinalizar().addActionListener(e -> onFinalizarVenda());
        caixaView.getBtnCancelar().addActionListener(e -> onCancelarVenda());
    }

    // ==========================================
    // TRATADORES DE EVENTOS (HANDLERS)
    // ==========================================

    @Override
    public void onProdutosChanged(List<Produto> produtos) {
        // Atualiza a tabela do caixa com estritamente produtos ativos
        caixaView.atualizarProdutos(produtoService.listarAtivos());
    }

    private void onAdicionarAoCarrinho() {
        Produto produtoSelecionado = caixaView.getProdutoSelecionado();
        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Selecione um produto na tabela de produtos disponíveis.",
                    "Atenção - Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validação preemptiva de status ativo do produto
        try {
            produtoService.validarDisponibilidadeParaVenda(produtoSelecionado);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    ex.getMessage(),
                    "Produto Indisponível",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantidadeTexto = caixaView.getQuantidadeTexto();
        Integer quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto);
            if (quantidade <= 0) {
                throw new IllegalArgumentException("A quantidade deve ser um número inteiro positivo.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Quantidade inválida. Informe um número inteiro positivo (ex.: 2).",
                    "Validação de Dados",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verifica se o produto já está no carrinho (evita duplicatas)
        // (A view permite adicionar múltiplas vezes; o controle de duplicidade
        // fica a cargo da regra de negócio no service, se necessário.)

        BigDecimal precoUnitario = produtoSelecionado.getPreco();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

        ItemCarrinho itemCarrinho = new ItemCarrinho(
                produtoSelecionado.getId(),
                produtoSelecionado.getNome(),
                quantidade,
                precoUnitario
        );

        // Adiciona à tabela visual do carrinho
        caixaView.adicionarItemCarrinho(
                itemCarrinho.getNomeProduto(),
                itemCarrinho.getQuantidade(),
                itemCarrinho.getPrecoUnitario(),
                itemCarrinho.getSubtotal()
        );

        // Atualiza o total parcial
        atualizarTotalParcial();

        JOptionPane.showMessageDialog(
                mainFrame,
                String.format("Item adicionado ao carrinho:\n%s x%d - Subtotal: %s",
                        produtoSelecionado.getNome(), quantidade,
                        MOEDA_FORMATTER.format(subtotal)),
                "Item Adicionado",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void onRemoverDoCarrinho() {
        int selectedRow = caixaView.getCarrinhoSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Selecione um item na tabela do carrinho para remover.",
                    "Atenção - Seleção Necessária",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        caixaView.removerItemCarrinhoSelecionado();
        atualizarTotalParcial();
    }

    private void onLimparCarrinho() {
        int confirmacao = JOptionPane.showConfirmDialog(
                mainFrame,
                "Deseja realmente limpar todos os itens do carrinho?",
                "Confirmar Limpeza do Carrinho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            caixaView.limparCarrinho();
            atualizarTotalParcial();
        }
    }

    private void onFinalizarVenda() {
        // Valida se há itens no carrinho
        int rowCount = caixaView.getTabelaCarrinho().getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "O carrinho está vazio. Adicione ao menos um item antes de finalizar a venda.",
                    "Atenção - Carrinho Vazio",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Constrói a lista de itens do carrinho a partir da tabela visual
        List<ItemCarrinho> carrinho = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            String nomeProduto = (String) caixaView.getTabelaCarrinho().getValueAt(i, 0);
            Integer quantidade = (Integer) caixaView.getTabelaCarrinho().getValueAt(i, 1);
            BigDecimal precoUnitario = (BigDecimal) caixaView.getTabelaCarrinho().getValueAt(i, 2);

            // Busca o produto pelo nome para obter o ID
            Produto produto = produtoService.buscarPorNome(nomeProduto).stream()
                    .findFirst()
                    .orElseGet(() -> produtoService.listarTodos().stream()
                            .filter(p -> p.getNome().equalsIgnoreCase(nomeProduto))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Produto não encontrado: " + nomeProduto)));

            carrinho.add(new ItemCarrinho(
                    produto.getId(),
                    nomeProduto,
                    quantidade,
                    precoUnitario
            ));
        }

        String clienteNome = caixaView.getClienteNome();
        Long clienteId = (clienteNome == null || clienteNome.isBlank()) ? null : null;

        try {
            Venda venda = vendaService.finalizarVenda(carrinho, clienteId, clienteNome);

            JOptionPane.showMessageDialog(
                    mainFrame,
                    String.format("Venda finalizada com sucesso!\n\nVenda #%d\nTotal: %s\nCliente: %s",
                            venda.getId(),
                            MOEDA_FORMATTER.format(venda.getTotal()),
                            (venda.getClienteNome() != null && !venda.getClienteNome().isBlank())
                                    ? venda.getClienteNome() : "(sem cliente)"),
                            "Venda Finalizada",
                            JOptionPane.INFORMATION_MESSAGE);

            // Limpa o carrinho após sucesso
            caixaView.limparCarrinho();
            atualizarTotalParcial();
            caixaView.getTxtCliente().setText("");

        } catch (IllegalStateException | IllegalArgumentException ex) {
            // Erros de validação amigáveis (produto inativo, estoque insuficiente, etc.)
            JOptionPane.showMessageDialog(
                    mainFrame,
                    ex.getMessage(),
                    "Atenção - Operação Não Permitida",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Erros inesperados de infraestrutura ou banco
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "Falha ao finalizar venda: " + ex.getMessage(),
                    "Erro no Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelarVenda() {
        int confirmacao = JOptionPane.showConfirmDialog(
                mainFrame,
                "Deseja cancelar a venda atual e limpar o carrinho?",
                "Confirmar Cancelamento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            caixaView.limparCarrinho();
            atualizarTotalParcial();
            caixaView.getTxtCliente().setText("");
            caixaView.getTxtQuantidade().setText("1");
        }
    }

    /**
     * Recalcula e atualiza o total parcial exibido no rodapé da view,
     * somando os subtotais de todos os itens na tabela do carrinho.
     */
    private void atualizarTotalParcial() {
        BigDecimal total = BigDecimal.ZERO;
        int rowCount = caixaView.getTabelaCarrinho().getRowCount();
        for (int i = 0; i < rowCount; i++) {
            BigDecimal subtotal = (BigDecimal) caixaView.getTabelaCarrinho().getValueAt(i, 3);
            if (subtotal != null) {
                total = total.add(subtotal);
            }
        }
        caixaView.atualizarTotal(MOEDA_FORMATTER.format(total));
    }

    // ==========================================
    // IMPLEMENTAÇÃO DO OBSERVER PATTERN
    // ==========================================

    @Override
    public void onVendasChanged(List<Venda> vendas) {
        // Atualiza a tabela de histórico de vendas quando o Service sinaliza
        // alterações (após uma venda finalizada com sucesso)
        vendaTableModel.setVendas(vendas);
    }

    /**
     * Retorna o modelo de tabela de vendas para uso na tela de histórico.
     *
     * @return {@link VendaTableModel} populado com as vendas mais recentes.
     */
    public VendaTableModel getVendaTableModel() {
        return vendaTableModel;
    }
}
