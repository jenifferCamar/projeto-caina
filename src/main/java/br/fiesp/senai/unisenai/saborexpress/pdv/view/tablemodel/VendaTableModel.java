package br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Modelo de Tabela customizado para o componente {@link javax.swing.JTable},
 * especializado na exibição de instâncias de {@link Venda} (histórico de vendas).
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC no Swing / Model-Delegate):</b>
 * <ul>
 * <li>O Swing adota uma variação do MVC onde o componente visual
 * ({@code JTable}) desacopla totalmente a renderização dos dados brutos através
 * da abstração {@link javax.swing.table.TableModel}.</li>
 * <li>Ao estender {@link AbstractTableModel}, implementamos os métodos
 * fundamentais {@code getRowCount()}, {@code getColumnCount()} e
 * {@code getValueAt()}.</li>
 * <li>Notificação Reativa: Quando a lista interna de vendas é atualizada,
 * invocamos {@code fireTableDataChanged()}, notificando a JTable para que ela
 * repinte as linhas de forma atômica e eficiente na Event Dispatch Thread (EDT).</li>
 * <li>Utilizado pela {@code VendaController} como observador de
 * {@code VendaService} para manter o histórico de vendas sempre sincronizado
 * após cada finalização.</li>
 * </ul>
 *
 * @author Cainã Antunes Silva
 */
public class VendaTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {
            "ID",
            "Data/Hora",
            "Cliente",
            "Total",
            "Status"
    };

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat
            .getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private static final SimpleDateFormat DATA_HORA_FORMATTER = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm");

    private List<Venda> vendas;

    /**
     * Construtor padrão inicializando a coleção vazia.
     */
    public VendaTableModel() {
        this.vendas = new ArrayList<>();
    }

    /**
     * Construtor recebendo uma lista inicial de vendas.
     *
     * @param vendas Lista de vendas a ser exibida.
     */
    public VendaTableModel(List<Venda> vendas) {
        this.vendas = (vendas != null) ? new ArrayList<>(vendas) : new ArrayList<>();
    }

    /**
     * Atualiza a lista completa de vendas e notifica os ouvintes da JTable.
     *
     * @param novasVendas Nova coleção de vendas obtida do serviço de negócio.
     */
    public void setVendas(List<Venda> novasVendas) {
        this.vendas = (novasVendas != null) ? new ArrayList<>(novasVendas) : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * Retorna a entidade {@link Venda} correspondente à linha selecionada na
     * tabela.
     *
     * @param rowIndex Índice da linha na JTable.
     * @return {@link Venda} associada, ou {@code null} se o índice for inválido.
     */
    public Venda getVendaAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < vendas.size()) {
            return vendas.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return vendas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUNAS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> Long.class;
            case 1, 2, 4 -> String.class;
            case 3 -> BigDecimal.class;
            default -> Object.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Células da tabela são somente leitura; o histórico de vendas não é editável
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Venda venda = vendas.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> venda.getId();
            case 1 -> DATA_HORA_FORMATTER.format(java.util.Date.from(
                    venda.getDataHora() != null ? venda.getDataHora().atZone(java.time.ZoneId.systemDefault())
                            .toInstant() : java.time.Instant.now()));
            case 2 -> (venda.getClienteNome() != null && !venda.getClienteNome().isBlank())
                    ? venda.getClienteNome()
                    : "(sem cliente)";
            case 3 -> venda.getTotal() != null ? venda.getTotal() : BigDecimal.ZERO;
            case 4 -> venda.getStatus();
            default -> null;
        };
    }
}
