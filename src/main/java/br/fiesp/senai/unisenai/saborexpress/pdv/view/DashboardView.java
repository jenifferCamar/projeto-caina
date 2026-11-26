package br.fiesp.senai.unisenai.saborexpress.pdv.view;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.MetricasVendasDTO;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.observer.VendaObserver;
import br.fiesp.senai.unisenai.saborexpress.pdv.model.service.VendaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Visão (View) do Dashboard de Indicadores de Vendas do PDV.
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC + Observer):</b>
 * <ul>
 * <li>Apresenta os três indicadores principais de vendas em cards visuais:
 * Quantidade de Vendas, Faturamento Total e Ticket Médio.</li>
 * <li>Implementa {@link VendaObserver} para ser notificada de forma reativa
 * sempre que uma venda é finalizada ou cancelada, atualizando os valores
 * exibidos automaticamente.</li>
 * <li>Não acessa repositórios nem banco de dados diretamente: recebe os dados
 * já calculados pelo {@link VendaService} via injeção de dependência no
 * construtor.</li>
 * <li>Suporta filtros de período (data início e fim) para consultar métricas
 * em intervalos específicos.</li>
 * <li>Toda atualização da interface ocorre na Event Dispatch Thread (EDT) do
 * Swing, garantida pelo despacho já realizado no serviço.</li>
 * </ul>
 */
public class DashboardView extends JPanel implements VendaObserver {

    private static final long serialVersionUID = 1L;

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat
            .getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private static final DateTimeFormatter DATA_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy");

    private final VendaService vendaService;

    private final JLabel lblTitulo;
    private final JLabel lblSubtitulo;

    // --- Cards de métricas ---
    private final JLabel lblQuantidadeVendas;
    private final JLabel lblFaturamentoTotal;
    private final JLabel lblTicketMedio;

    // --- Filtros de período ---
    private final JTextField txtDataInicio;
    private final JTextField txtDataFim;
    private final JButton btnAplicarFiltro;
    private final JButton btnLimparFiltro;

    /**
     * Construtor recebendo o serviço de vendas para cálculo de métricas.
     *
     * @param vendaService Camada de serviços de vendas (não pode ser nulo).
     */
    public DashboardView(VendaService vendaService) {
        this.vendaService = vendaService;

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ==========================================
        // 1. CABEÇALHO
        // ==========================================
        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new BoxLayout(painelCabecalho, BoxLayout.Y_AXIS));

        lblTitulo = new JLabel("Dashboard de Vendas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        lblSubtitulo = new JLabel("Indicadores de desempenho baseados em vendas finalizadas.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(Color.GRAY);

        painelCabecalho.add(lblTitulo);
        painelCabecalho.add(Box.createVerticalStrut(6));
        painelCabecalho.add(lblSubtitulo);
        add(painelCabecalho, BorderLayout.NORTH);

        // ==========================================
        // 2. PAINEL DE FILTROS DE PERÍODO
        // ==========================================
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        painelFiltros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Filtrar por Período "),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel lblInicio = new JLabel("Data Início:");
        lblInicio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDataInicio = new JTextField(10);
        txtDataInicio.setToolTipText("Formato: dd/MM/yyyy");

        JLabel lblFim = new JLabel("Data Fim:");
        lblFim.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDataFim = new JTextField(10);
        txtDataFim.setToolTipText("Formato: dd/MM/yyyy");

        btnAplicarFiltro = new JButton("Aplicar Filtro");
        btnAplicarFiltro.setPreferredSize(new Dimension(110, 30));
        btnAplicarFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnLimparFiltro = new JButton("Limpar Filtro");
        btnLimparFiltro.setPreferredSize(new Dimension(110, 30));
        btnLimparFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        painelFiltros.add(lblInicio);
        painelFiltros.add(txtDataInicio);
        painelFiltros.add(lblFim);
        painelFiltros.add(txtDataFim);
        painelFiltros.add(btnAplicarFiltro);
        painelFiltros.add(btnLimparFiltro);

        add(painelFiltros, BorderLayout.NORTH);

        // ==========================================
        // 3. CARDS DE MÉTRICAS
        // ==========================================
        JPanel painelCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        painelCards.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblQuantidadeVendas = criarCardMetrica("Vendas Realizadas", "0");
        lblFaturamentoTotal = criarCardMetrica("Faturamento Total", "R$ 0,00");
        lblTicketMedio = criarCardMetrica("Ticket Médio", "R$ 0,00");

        painelCards.add(lblQuantidadeVendas);
        painelCards.add(lblFaturamentoTotal);
        painelCards.add(lblTicketMedio);

        add(painelCards, BorderLayout.CENTER);

        // ==========================================
        // 4. RODAPÉ
        // ==========================================
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblRodape = new JLabel("Os valores são atualizados automaticamente após cada finalização ou cancelamento de venda.");
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRodape.setForeground(Color.GRAY);
        painelRodape.add(lblRodape, BorderLayout.WEST);

        add(painelRodape, BorderLayout.SOUTH);

        // ==========================================
        // 5. OUVINTES DOS FILTROS
        // ==========================================
        btnAplicarFiltro.addActionListener(e -> aplicarFiltroPeriodo());
        btnLimparFiltro.addActionListener(e -> limparFiltro());

        // Inicializa com valores zerados
        atualizarMetricas();
    }

    /**
     * Cria um card de metrica com título e valor.
     *
     * @param titulo       Título da métrica (ex: "Vendas Realizadas").
     * @param valorInicial Valor inicial exibido (ex: "0" ou "R$ 0,00").
     * @return Label contendo o valor da métrica (para atualização posterior).
     */
    private JLabel criarCardMetrica(String titulo, String valorInicial) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        card.setPreferredSize(new Dimension(220, 120));
        card.setMaximumSize(new Dimension(240, 140));
        card.setBackground(Color.WHITE);

        JLabel lblTituloCard = new JLabel(titulo);
        lblTituloCard.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTituloCard.setForeground(Color.GRAY);

        JLabel lblValorCard = new JLabel(valorInicial, SwingConstants.CENTER);
        lblValorCard.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValorCard.setForeground(new Color(30, 30, 30));

        card.add(lblTituloCard, BorderLayout.NORTH);
        card.add(lblValorCard, BorderLayout.CENTER);

        add(card);

        return lblValorCard;
    }

    /**
     * Solicita as métricas atualizadas ao serviço (sem filtro de período) e
     * repinta os cards.
     */
    private void atualizarMetricas() {
        MetricasVendasDTO metricas = vendaService.calcularMetricas();
        aplicarMetricas(metricas);
    }

    /**
     * Aplica as métricas recebidas aos cards da interface.
     *
     * @param metricas DTO com as métricas calculadas (não pode ser nulo; campos
     *                 numéricos são tratados como {@code ZERO} se nulos).
     */
    private void aplicarMetricas(MetricasVendasDTO metricas) {
        Long quantidade = metricas.getQuantidadeVendas();
        BigDecimal faturamento = metricas.getFaturamentoTotal();
        BigDecimal ticketMedio = metricas.getTicketMedio();

        lblQuantidadeVendas.setText(quantidade != null ? quantidade.toString() : "0");
        lblFaturamentoTotal.setText(MOEDA_FORMATTER.format(
                faturamento != null ? faturamento : BigDecimal.ZERO));
        lblTicketMedio.setText(MOEDA_FORMATTER.format(
                ticketMedio != null ? ticketMedio : BigDecimal.ZERO));
    }

    /**
     * Lê os campos de data início e fim, converte para {@link LocalDateTime}
     * e solicita métricas filtradas ao serviço.
     */
    private void aplicarFiltroPeriodo() {
        LocalDateTime inicio = null;
        LocalDateTime fim = null;

        String textoInicio = txtDataInicio.getText().trim();
        String textoFim = txtDataFim.getText().trim();

        if (!textoInicio.isEmpty()) {
            try {
                LocalDate dataInicio = LocalDate.parse(textoInicio, DATA_FORMATTER);
                inicio = dataInicio.atStartOfDay();
            } catch (DateTimeParseException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Data de início inválida. Use o formato dd/MM/yyyy.",
                        "Erro de Filtro",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (!textoFim.isEmpty()) {
            try {
                LocalDate dataFim = LocalDate.parse(textoFim, DATA_FORMATTER);
                fim = dataFim.atTime(LocalTime.MAX);
            } catch (DateTimeParseException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Data de fim inválida. Use o formato dd/MM/yyyy.",
                        "Erro de Filtro",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        MetricasVendasDTO metricas = vendaService.calcularMetricas(inicio, fim);
        aplicarMetricas(metricas);
    }

    /**
     * Limpa os campos de filtro e recarrega as métricas sem período.
     */
    private void limparFiltro() {
        txtDataInicio.setText("");
        txtDataFim.setText("");
        atualizarMetricas();
    }

    // ==========================================
    // IMPLEMENTAÇÃO DO PADRÃO OBSERVER (VendaObserver)
    // ==========================================

    /**
     * Notificação disparada pelo {@link VendaService} quando ocorre uma nova
     * venda finalizada ou uma venda cancelada. Solicita as métricas atualizadas
     * ao serviço e repinta os cards na Event Dispatch Thread (EDT).
     *
     * @param vendas Lista atualizada de todas as vendas registradas.
     */
    @Override
    public void onVendasChanged(List<Venda> vendas) {
        // O despacho já ocorre dentro da EDT via SwingUtilities.invokeLater no serviço.
        // Solicita métricas atualizadas ao serviço (consultas agregadas no banco).
        atualizarMetricas();
    }
}
