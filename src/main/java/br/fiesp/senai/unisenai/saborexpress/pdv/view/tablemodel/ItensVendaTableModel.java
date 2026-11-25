package br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.ItemVenda;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItensVendaTableModel extends AbstractTableModel {
    private static final String[] COLUNAS = {"Produto", "Qtd.", "Preço Unit.", "Subtotal"};
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private List<ItemVenda> itens = new ArrayList<>();

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return itens.size(); }
    @Override public int getColumnCount() { return COLUNAS.length; }
    @Override public String getColumnName(int column) { return COLUNAS[column]; }
    @Override public boolean isCellEditable(int r, int c) { return false; }
    @Override public Class<?> getColumnClass(int c) { return c == 1 ? Integer.class : c >= 2 ? BigDecimal.class : String.class; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ItemVenda item = itens.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getProduto() != null ? item.getProduto().getNome() : "";
            case 1 -> item.getQuantidade();
            case 2 -> item.getPrecoUnitario();
            case 3 -> item.getSubtotal();
            default -> null;
        };
    }
}
