package br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Modelo de Tabela customizado para o componente {@link javax.swing.JTable},
 * especializado na exibição dos itens de uma {@link br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Venda}
 * (detalhes da venda selecionada).
 * <p>
 * <b>Conceito Pedagógico (Padrão MVC no Swing / Model-Delegate):</b>
 * <ul>
 * <li>O Swing adota uma variação do MVC onde o componente visual
 * ({@code JTable}) desacopla totalmente a renderização dos dados brutos através
 * da abstração {@link javax.swing.table.TableModel}.</li>
 * <li>Ao estender {@link AbstractTableModel}, implementamos os métodos
 * fundamentais {@code getRowCount()}, {@code getColumnCount()} e
 * {@code getValueAt()}.</li>
 * <li>Notificação Reativa: Quando a lista interna de itens é atualizada,
 * invocamos {@code fireTableDataChanged()}, notificando a JTable para que ela
 * repinte as linhas de forma atômica e eficiente na Event Dispatch Thread (EDT).</li>
 * </ul>
 */
public class ItensVendaTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {
            "Produto",
            "Quantidade",
            "Preço Unitário",
            "Subtotal"
    };

    private static final NumberFormat MOEDA_FORMATTER = NumberFormat
            .getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    private List<ItemVenda> itens;

    /**
     * Construtor padrão inicializando a coleção vazia.
     */
    public ItensVendaTableModel() {
        this.itens = new ArrayList<>();
    }

    /**
     * Construtor recebendo uma lista inicial de itens.
     *
     * @param itens Lista de itens a ser exibida.
     */
    public ItensVendaTableModel(List<ItemVenda> itens) {
        this.itens = (itens != null) ? new ArrayList<>(itens) : new ArrayList<>();
    }

    /**
     * Atualiza a lista completa de itens e notifica os ouvintes da JTable.
     *
     * @param novosItens Nova coleção de itens da venda selecionada.
     */
    public void setItens(List<ItemVenda> novosItens) {
        this.itens = (novosItens != null) ? new ArrayList<>(novosItens) : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * Retorna o {@link ItemVenda} correspondente à linha selecionada na tabela.
     *
     * @param rowIndex Índice da linha na JTable.
     * @return {@link ItemVenda} associado, ou {@code null} se o índice for inválido.
     */
    public ItemVenda getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < itens.size()) {
            return itens.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return itens.size();
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
            case 0 -> String.class;
            case 1 -> Integer.class;
            case 2, 3 -> BigDecimal.class;
            default -> Object.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Células da tabela são somente leitura; os detalhes da venda não são editáveis
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemVenda item = itens.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> (item.getProduto() != null) ? item.getProduto().getNome() : "(sem produto)";
            case 1 -> item.getQuantidade();
            case 2 -> item.getPrecoUnitario() != null ? item.getPrecoUnitario() : BigDecimal.ZERO;
            case 3 -> item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO;
            default -> null;
        };
    }
}
