import java.awt.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * BaseCrudWindow – shared scaffold for all CRUD detail windows.
 * Subclasses supply: table name, column definitions, and field widgets.
 */
public abstract class BaseCrudWindow extends JFrame {

    protected static final String DB_URL  = "jdbc:mysql://localhost:3306/expanses?useSSL=false";
    protected static final String DB_USER = "root";
    protected static final String DB_PASS = "tiger";

    protected Connection        con = null;
    protected PreparedStatement pst = null;
    protected ResultSet         rs  = null;

    protected JTable          table;
    protected DefaultTableModel tableModel;

    protected JButton btnSave, btnFind, btnUpdate, btnDelete, btnNew, btnSearch, btnHome;

    // ── DB ────────────────────────────────────────────────────────
    protected void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (con == null || con.isClosed())
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "DB connection failed:\n" + ex.getMessage());
        }
    }

    protected void loadTable(String sql) {
        try {
            if (con == null || con.isClosed()) connectDB();
            pst = con.prepareStatement(sql);
            rs  = pst.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] names = new String[cols];
            for (int i = 0; i < cols; i++) names[i] = meta.getColumnLabel(i + 1);
            tableModel = new DefaultTableModel(names, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                tableModel.addRow(row);
            }
            table.setModel(tableModel);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load Error:\n" + ex.getMessage());
        }
    }

    // ── Shared UI helpers ─────────────────────────────────────────
    protected JLabel makeLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.RIGHT);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 17));
        l.setForeground(new Color(51, 0, 51));
        return l;
    }

    protected JTextField makeField(int w) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(w, 30));
        return f;
    }

    protected JComboBox<String> makeCombo(String... items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        c.setPreferredSize(new Dimension(180, 30));
        return c;
    }

    protected JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monotype Corsiva", Font.BOLD, 15));
        b.setBackground(new Color(0, 220, 220));
        b.setForeground(new Color(51, 0, 51));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    protected JScrollPane makeTablePane(int height) {
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.setSelectionBackground(new Color(0, 200, 200));
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, height));
        return sp;
    }

    protected JPanel makeTitlePanel(String titleText) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(titleText, SwingConstants.CENTER);
        t.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        t.setForeground(new Color(51, 51, 51));
        t.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        p.add(t, BorderLayout.CENTER);
        btnHome = makeButton("Home");
        JPanel hw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        hw.setOpaque(false); hw.add(btnHome);
        p.add(hw, BorderLayout.EAST);
        return p;
    }

    protected JPanel makeButtonRow(JButton... buttons) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        p.setOpaque(false);
        for (JButton b : buttons) p.add(b);
        return p;
    }

    protected JPanel makeTitledForm(String borderTitle) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 180), 1),
            borderTitle, 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), new Color(0, 100, 120)));
        return p;
    }

    protected GridBagConstraints lCon(int gridx) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = gridx;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(6, gridx == 0 ? 8 : 24, 6, 8);
        return c;
    }

    protected GridBagConstraints fCon(int gridx) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx   = gridx;
        c.anchor  = GridBagConstraints.WEST;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets  = new Insets(6, 0, 6, 10);
        return c;
    }

    protected String cell(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    protected void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showOk(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void goHome() {
        new MdiForm().setVisible(true);
        dispose();
    }
}