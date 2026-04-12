import java.sql.*;
import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 * Pure-Java replacement for net.proteanit.sql.DbUtils.resultSetToTableModel().
 * Usage:  table.setModel(new ResultSetTableModel(resultSet));
 */
public class ResultSetTableModel extends AbstractTableModel {

    private final List<String>       columnNames = new ArrayList<>();
    private final List<List<Object>> rows        = new ArrayList<>();

    public ResultSetTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            columnNames.add(meta.getColumnLabel(i));
        }
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= cols; i++) {
                row.add(rs.getObject(i));
            }
            rows.add(row);
        }
    }

    @Override public int    getRowCount()              { return rows.size(); }
    @Override public int    getColumnCount()           { return columnNames.size(); }
    @Override public String getColumnName(int col)     { return columnNames.get(col); }
    @Override public boolean isCellEditable(int r, int c) { return false; }

    @Override
    public Object getValueAt(int row, int col) {
        Object val = rows.get(row).get(col);
        return val == null ? "" : val;
    }
}