import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * BaseQueryWindow – shared scaffold for all search/query windows.
 * Provides: dropdown field selector, value input, Search / Show All / Clear / Home buttons.
 */
public abstract class BaseQueryWindow extends BaseCrudWindow {

    private final String   tableName;
    private final String[] displayNames;   // human-readable column names for combo
    private final String[] columnNames;    // actual DB column names

    private JComboBox<String> cmbField;
    private JTextField        txtValue;
    private JButton           btnSearch, btnShowAll, btnClear;

    protected BaseQueryWindow(String title, String tableName, String[][] fieldMap) {
        this.tableName    = tableName;
        this.displayNames = new String[fieldMap.length];
        this.columnNames  = new String[fieldMap.length];
        for (int i = 0; i < fieldMap.length; i++) {
            displayNames[i] = fieldMap[i][0];
            columnNames[i]  = fieldMap[i][1];
        }
        buildUI(title);
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – " + title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 580);
        setLocationRelativeTo(null);
        connectDB();
        ensureTable();
        loadTable("SELECT * FROM " + tableName);
    }

    /** Subclasses may override to create the table if it doesn't exist. */
    protected void ensureTable() { /* override if needed */ }

    private void buildUI(String titleText) {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        // ── Title ──
        root.add(makeTitlePanel(titleText), BorderLayout.NORTH);

        // ── Query bar ──
        JPanel queryBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        queryBar.setOpaque(false);
        queryBar.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 180)),
            "Search Filter", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 100, 120)));

        queryBar.add(new JLabel("Search By :") {{
            setFont(new Font("Monotype Corsiva", Font.BOLD, 17));
            setForeground(new Color(51,0,51));
        }});

        cmbField = new JComboBox<>(displayNames);
        cmbField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbField.setPreferredSize(new Dimension(180, 30));
        queryBar.add(cmbField);

        queryBar.add(new JLabel("Value :") {{
            setFont(new Font("Monotype Corsiva", Font.BOLD, 17));
            setForeground(new Color(51,0,51));
        }});

        txtValue = new JTextField();
        txtValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtValue.setPreferredSize(new Dimension(220, 30));
        queryBar.add(txtValue);

        btnSearch  = makeButton("Search");
        btnShowAll = makeButton("Show All");
        btnClear   = makeButton("Clear");
        queryBar.add(btnSearch);
        queryBar.add(btnShowAll);
        queryBar.add(btnClear);

        root.add(queryBar, BorderLayout.CENTER);

        // ── Result table ──
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(makeTablePane(340), BorderLayout.CENTER);

        // Row count label
        JLabel lblCount = new JLabel("Rows: 0");
        lblCount.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCount.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));
        south.add(lblCount, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        // ── Actions ──
        btnSearch.addActionListener(e -> {
            String val = txtValue.getText().trim();
            if (val.isEmpty()) { showErr("Please enter a value to search."); return; }
            int idx    = cmbField.getSelectedIndex();
            String col = columnNames[idx];
            try {
                pst = con.prepareStatement(
                    "SELECT * FROM " + tableName + " WHERE " + col + " LIKE ?");
                pst.setString(1, "%" + val + "%");
                rs = pst.executeQuery();
                populateTableFromRS(rs);
                lblCount.setText("Rows: " + tableModel.getRowCount());
            } catch (SQLException ex) { showErr(ex.getMessage()); }
        });

        btnShowAll.addActionListener(e -> {
            loadTable("SELECT * FROM " + tableName);
            lblCount.setText("Rows: " + (tableModel == null ? 0 : tableModel.getRowCount()));
        });

        btnClear.addActionListener(e -> {
            txtValue.setText("");
            cmbField.setSelectedIndex(0);
            txtValue.requestFocus();
        });

        btnHome.addActionListener(e -> goHome());

        txtValue.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnSearch.doClick();
            }
        });
    }

    private void populateTableFromRS(ResultSet rset) {
        try {
            ResultSetMetaData meta = rset.getMetaData();
            int cols = meta.getColumnCount();
            String[] names = new String[cols];
            for (int i = 0; i < cols; i++) names[i] = meta.getColumnLabel(i + 1);
            tableModel = new DefaultTableModel(names, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            while (rset.next()) {
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rset.getObject(i + 1);
                tableModel.addRow(row);
            }
            table.setModel(tableModel);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        } catch (SQLException ex) {
            showErr("Error loading results:\n" + ex.getMessage());
        }
    }
}