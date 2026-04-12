import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.*;

/**
 * BaseReportWindow – shared scaffold for all report windows.
 * Features: full data table, date-range filter, Print, Export CSV, Home.
 */
public abstract class BaseReportWindow extends BaseCrudWindow {

    private final String reportTitle;
    private final String tableName;
    private final String totalColumn;   // column to SUM for totals row (null = skip)
    
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterField;
    private final String[]   filterDisplayNames;
    private final String[]   filterColumnNames;
    private JButton   btnFilter, btnShowAll, btnPrint, btnExportCSV;
    private JLabel    lblSummary;

    protected BaseReportWindow(String reportTitle, String tableName,
                                String totalColumn, String[][] filterFields) {
        this.reportTitle  = reportTitle;
        this.tableName    = tableName;
        this.totalColumn  = totalColumn;
        this.filterDisplayNames = new String[filterFields.length];
        this.filterColumnNames  = new String[filterFields.length];
        for (int i = 0; i < filterFields.length; i++) {
            filterDisplayNames[i] = filterFields[i][0];
            filterColumnNames[i]  = filterFields[i][1];
        }
        buildUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – " + reportTitle);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        connectDB();
        ensureTable();
        loadTable("SELECT * FROM " + tableName);
        updateSummary();
    }

    protected void ensureTable() { /* override in subclass if needed */ }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        // ── Title ──
        root.add(makeTitlePanel(reportTitle), BorderLayout.NORTH);

        // ── Filter bar ──
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterBar.setOpaque(false);
        filterBar.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 180)),
            "Filters & Actions", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0, 100, 120)));

        filterBar.add(label("Filter By :"));
        cmbFilterField = new JComboBox<>(filterDisplayNames);
        cmbFilterField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbFilterField.setPreferredSize(new Dimension(160, 28));
        filterBar.add(cmbFilterField);

        filterBar.add(label("Value :"));
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setPreferredSize(new Dimension(180, 28));
        filterBar.add(txtSearch);

        btnFilter   = makeButton("Apply Filter");
        btnShowAll  = makeButton("Show All");
        btnPrint    = makeButton("🖨 Print");
        btnExportCSV= makeButton("⬇ Export CSV");
        filterBar.add(btnFilter);
        filterBar.add(btnShowAll);
        filterBar.add(btnPrint);
        filterBar.add(btnExportCSV);

        root.add(filterBar, BorderLayout.CENTER);

        // ── Table + summary ──
        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setOpaque(false);
        south.add(makeTablePane(380), BorderLayout.CENTER);

        lblSummary = new JLabel(" ");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSummary.setForeground(new Color(0, 80, 140));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 0));
        south.add(lblSummary, BorderLayout.SOUTH);

        root.add(south, BorderLayout.SOUTH);

        // ── Actions ──
        btnFilter.addActionListener(e -> applyFilter());
        btnShowAll.addActionListener(e -> { loadTable("SELECT * FROM " + tableName); updateSummary(); });
        btnPrint.addActionListener(e -> printReport());
        btnExportCSV.addActionListener(e -> exportCSV());
        btnHome.addActionListener(e -> goHome());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) applyFilter();
            }
        });
    }

    private void applyFilter() {
        String val = txtSearch.getText().trim();
        if (val.isEmpty()) { showErr("Enter a filter value."); return; }
        int idx = cmbFilterField.getSelectedIndex();
        String col = filterColumnNames[idx];
        try {
            pst = con.prepareStatement(
                "SELECT * FROM " + tableName + " WHERE " + col + " LIKE ?");
            pst.setString(1, "%" + val + "%");
            rs = pst.executeQuery();
            populateFromRS(rs);
            updateSummary();
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void populateFromRS(ResultSet rset) {
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
        } catch (SQLException ex) { showErr("Error:\n" + ex.getMessage()); }
    }

    private void updateSummary() {
        if (tableModel == null) return;
        int rows = tableModel.getRowCount();
        StringBuilder sb = new StringBuilder("Total Records: " + rows);

        // Sum numeric totalColumn if specified
        if (totalColumn != null && rows > 0) {
            // Find column index by name
            int colIdx = -1;
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                if (tableModel.getColumnName(c).equalsIgnoreCase(totalColumn)) {
                    colIdx = c; break;
                }
            }
            if (colIdx >= 0) {
                double total = 0;
                for (int r = 0; r < rows; r++) {
                    Object val = tableModel.getValueAt(r, colIdx);
                    if (val != null) {
                        try { total += Double.parseDouble(val.toString()); } catch (NumberFormatException ignored) {}
                    }
                }
                sb.append("    |    Total ").append(totalColumn).append(": ₹")
                  .append(String.format("%,.2f", total));
            }
        }
        lblSummary.setText(sb.toString());
    }

    // ── Print ─────────────────────────────────────────────────────
    private void printReport() {
        try {
            // Build a simple text-based printable
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName(reportTitle);
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                // Title
                g2.setFont(new Font("Monotype Corsiva", Font.BOLD, 18));
                g2.drawString(reportTitle, 20, 30);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.drawString("Generated: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), 20, 45);
                // Table header
                int y = 65, rowH = 14;
                if (tableModel == null) return Printable.PAGE_EXISTS;
                int cols = tableModel.getColumnCount();
                int colW = (int)(pageFormat.getImageableWidth() / Math.max(cols, 1)) - 2;
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                for (int c = 0; c < cols; c++) {
                    g2.drawString(tableModel.getColumnName(c), 20 + c * colW, y);
                }
                y += rowH;
                g2.drawLine(20, y, (int) pageFormat.getImageableWidth() - 20, y);
                y += 4;
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < cols; c++) {
                        Object val = tableModel.getValueAt(r, c);
                        g2.drawString(val == null ? "" : val.toString(), 20 + c * colW, y);
                    }
                    y += rowH;
                    if (y > pageFormat.getImageableHeight() - 20) break;
                }
                // Summary
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.drawString(lblSummary.getText(), 20, (int)pageFormat.getImageableHeight() - 10);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) job.print();
        } catch (PrinterException ex) {
            showErr("Print error:\n" + ex.getMessage());
        }
    }

    // ── Export CSV ────────────────────────────────────────────────
    private void exportCSV() {
        if (tableModel == null || tableModel.getRowCount() == 0) {
            showErr("No data to export.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(reportTitle.replace(" ", "_") + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
            // Header
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                if (c > 0) pw.print(",");
                pw.print("\"" + tableModel.getColumnName(c) + "\"");
            }
            pw.println();
            // Rows
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    if (c > 0) pw.print(",");
                    Object val = tableModel.getValueAt(r, c);
                    pw.print("\"" + (val == null ? "" : val.toString().replace("\"", "\"\"")) + "\"");
                }
                pw.println();
            }
            showOk("Exported to: " + fc.getSelectedFile().getAbsolutePath());
        } catch (IOException ex) {
            showErr("Export error:\n" + ex.getMessage());
        }
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 16));
        l.setForeground(new Color(51,0,51));
        return l;
    }
}