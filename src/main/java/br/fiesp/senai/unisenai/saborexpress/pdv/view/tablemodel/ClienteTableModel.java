package br.fiesp.senai.unisenai.saborexpress.pdv.view.tablemodel;

import br.fiesp.senai.unisenai.saborexpress.pdv.model.entity.Cliente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {"ID", "Nome", "Telefone", "Observações"};

    private List<Cliente> clientes;

    public ClienteTableModel() {
        this.clientes = new ArrayList<>();
    }

    public ClienteTableModel(List<Cliente> clientes) {
        this.clientes = (clientes != null) ? new ArrayList<>(clientes) : new ArrayList<>();
    }

    public void setClientes(List<Cliente> novosClientes) {
        this.clientes = (novosClientes != null) ? new ArrayList<>(novosClientes) : new ArrayList<>();
        fireTableDataChanged();
    }

    public Cliente getClienteAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < clientes.size()) {
            return clientes.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return clientes.size();
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
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cliente cliente = clientes.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> cliente.getId();
            case 1 -> cliente.getNome();
            case 2 -> cliente.getTelefone();
            case 3 -> cliente.getObservacoes();
            default -> null;
        };
    }
}
