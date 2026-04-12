import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

public class CustomerDetail extends JFrame {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/expanses?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tiger";

    private Connection        con = null;
    private ResultSet         rs  = null;
    private PreparedStatement pst;

    // Form fields
    private JTextField txtCusId, txtCustName, txtAddress, txtCity, txtPin;
    private JTextField txtNoOfPerson, txtIdentProof, txtContNo;
    private JTextField txtTotalRoom, txtDateOfBooking;

    // Table
    private JTable         table;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton btnSave, btnUpdate, btnDelete, btnNew, btnSearch, btnHome;

    public CustomerDetail() {
        initComponents();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Customer Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        connect();
        loadTable();
    }

    // ── DB ────────────────────────────────────────────────────────
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (con == null || con.isClosed())
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception ex) {
            Logger.getLogger(CustomerDetail.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "DB connection failed:\n" + ex.getMessage());
        }
    }

    private void loadTable() {
        try {
            if (con == null || con.isClosed()) connect();
            pst = con.prepareStatement("SELECT * FROM customerdetail");
            rs  = pst.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] colNames = new String[cols];
            for (int i = 0; i < cols; i++) colNames[i] = meta.getColumnLabel(i + 1);

            tableModel = new DefaultTableModel(colNames, 0) {
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
            JOptionPane.showMessageDialog(this, "Error loading table:\n" + ex.getMessage());
        }
    }

    // ── UI ────────────────────────────────────────────────────────
    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        // ── Title row ──
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = new JLabel("CUSTOMER DETAIL", SwingConstants.CENTER);
        title.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        title.setForeground(new Color(0, 51, 51));
        titleRow.add(title, BorderLayout.CENTER);

        btnHome = makeButton("Home");
        JPanel homeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        homeWrap.setOpaque(false);
        homeWrap.add(btnHome);
        titleRow.add(homeWrap, BorderLayout.EAST);
        root.add(titleRow, BorderLayout.NORTH);

        // ── Form panel (two columns) ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 180), 1),
            "Customer Information", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(0, 100, 120)));

        GridBagConstraints lc  = lCon();
        GridBagConstraints fc  = fCon();
        GridBagConstraints lc2 = lCon(); lc2.gridx = 2; lc2.insets = new Insets(6, 30, 6, 10);
        GridBagConstraints fc2 = fCon(); fc2.gridx = 3;

        // Left column
        lc.gridy = 0; fc.gridy = 0;
        formPanel.add(makeLabel("Customer ID :"), lc);
        txtCusId = makeField(150); formPanel.add(txtCusId, fc);

        lc.gridy = 1; fc.gridy = 1;
        formPanel.add(makeLabel("Customer Name :"), lc);
        txtCustName = makeField(190); formPanel.add(txtCustName, fc);

        lc.gridy = 2; fc.gridy = 2;
        formPanel.add(makeLabel("Address :"), lc);
        txtAddress = makeField(190); formPanel.add(txtAddress, fc);

        lc.gridy = 3; fc.gridy = 3;
        formPanel.add(makeLabel("City :"), lc);
        txtCity = makeField(150); formPanel.add(txtCity, fc);

        lc.gridy = 4; fc.gridy = 4;
        formPanel.add(makeLabel("Pin Code :"), lc);
        txtPin = makeField(120); formPanel.add(txtPin, fc);

        // Right column
        lc2.gridy = 0; fc2.gridy = 0;
        formPanel.add(makeLabel("Identity Proof :"), lc2);
        txtIdentProof = makeField(190); formPanel.add(txtIdentProof, fc2);

        lc2.gridy = 1; fc2.gridy = 1;
        formPanel.add(makeLabel("Contact No. :"), lc2);
        txtContNo = makeField(150); formPanel.add(txtContNo, fc2);

        lc2.gridy = 2; fc2.gridy = 2;
        formPanel.add(makeLabel("No. of Persons :"), lc2);
        txtNoOfPerson = makeField(100); formPanel.add(txtNoOfPerson, fc2);

        lc2.gridy = 3; fc2.gridy = 3;
        formPanel.add(makeLabel("No. of Rooms :"), lc2);
        txtTotalRoom = makeField(100); formPanel.add(txtTotalRoom, fc2);

        lc2.gridy = 4; fc2.gridy = 4;
        formPanel.add(makeLabel("Booking Date :"), lc2);
        txtDateOfBooking = makeField(150); formPanel.add(txtDateOfBooking, fc2);

        root.add(formPanel, BorderLayout.CENTER);

        // ── Buttons ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnRow.setOpaque(false);
        btnSave   = makeButton("Save");
        btnUpdate = makeButton("Update");
        btnDelete = makeButton("Delete");
        btnNew    = makeButton("Add / New");
        btnSearch = makeButton("Search");
        btnRow.add(btnSave);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnNew);
        btnRow.add(btnSearch);

        // ── Table ──
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.setSelectionBackground(new Color(0, 200, 200));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 200));

        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setOpaque(false);
        south.add(btnRow,  BorderLayout.NORTH);
        south.add(scroll,  BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        // ── Actions ──
        btnSave.addActionListener(e -> saveRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnNew.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchRecord());
        btnHome.addActionListener(e -> { new MdiForm().setVisible(true); dispose(); });

        // ── Row click → populate form ──
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { populateFromTable(); }
        });
    }

    // ── CRUD ─────────────────────────────────────────────────────
    private void saveRecord() {
        if (!validateInput()) return;
        try {
            pst = con.prepareStatement(
                "INSERT INTO customerdetail(cusid,custname,address,city,pincode," +
                "noofperson,identityproof,contno,noofroom,dateofbooking) VALUES(?,?,?,?,?,?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Record Saved Successfully!");
                clearFields();
                loadTable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Save Error:\n" + ex.getMessage());
        }
    }

    private void updateRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a row in the table to update.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cusid = tableModel.getValueAt(row, 0).toString();
        if (!validateInput()) return;
        try {
            pst = con.prepareStatement(
                "UPDATE customerdetail SET custname=?,address=?,city=?,pincode=?," +
                "noofperson=?,identityproof=?,contno=?,noofroom=?,dateofbooking=? WHERE cusid=?");
            pst.setString(1,  txtCustName.getText().trim());
            pst.setString(2,  txtAddress.getText().trim());
            pst.setString(3,  txtCity.getText().trim());
            pst.setString(4,  txtPin.getText().trim());
            pst.setString(5,  txtNoOfPerson.getText().trim());
            pst.setString(6,  txtIdentProof.getText().trim());
            pst.setString(7,  txtContNo.getText().trim());
            pst.setString(8,  txtTotalRoom.getText().trim());
            pst.setString(9,  txtDateOfBooking.getText().trim());
            pst.setString(10, cusid);
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed – customer not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update Error:\n" + ex.getMessage());
        }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Customer ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete customer ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM customerdetail WHERE cusid=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Record Deleted Successfully!");
                clearFields();
                loadTable();
            } else {
                JOptionPane.showMessageDialog(this, "Record Not Found!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete Error:\n" + ex.getMessage());
        }
    }

    private void searchRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Customer ID to search:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM customerdetail WHERE cusid=?");
            pst.setString(1, id.trim());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtCusId.setText(rs.getString(1));
                txtCustName.setText(rs.getString(2));
                txtAddress.setText(rs.getString(3));
                txtCity.setText(rs.getString(4));
                txtPin.setText(rs.getString(5));
                txtNoOfPerson.setText(rs.getString(6));
                txtIdentProof.setText(rs.getString(7));
                txtContNo.setText(rs.getString(8));
                txtTotalRoom.setText(rs.getString(9));
                txtDateOfBooking.setText(rs.getString(10));
            } else {
                JOptionPane.showMessageDialog(this, "No Record Found!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search Error:\n" + ex.getMessage());
        }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1,  txtCusId.getText().trim());
        p.setString(2,  txtCustName.getText().trim());
        p.setString(3,  txtAddress.getText().trim());
        p.setString(4,  txtCity.getText().trim());
        p.setString(5,  txtPin.getText().trim());
        p.setString(6,  txtNoOfPerson.getText().trim());
        p.setString(7,  txtIdentProof.getText().trim());
        p.setString(8,  txtContNo.getText().trim());
        p.setString(9,  txtTotalRoom.getText().trim());
        p.setString(10, txtDateOfBooking.getText().trim());
    }

    private boolean validateInput() {
        if (txtCusId.getText().trim().isEmpty()
                || txtCustName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Customer ID and Customer Name are required.",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void populateFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        txtCusId.setText(nullToEmpty(m.getValueAt(row, 0)));
        txtCustName.setText(nullToEmpty(m.getValueAt(row, 1)));
        txtAddress.setText(nullToEmpty(m.getValueAt(row, 2)));
        txtCity.setText(nullToEmpty(m.getValueAt(row, 3)));
        txtPin.setText(nullToEmpty(m.getValueAt(row, 4)));
        txtNoOfPerson.setText(nullToEmpty(m.getValueAt(row, 5)));
        txtIdentProof.setText(nullToEmpty(m.getValueAt(row, 6)));
        txtContNo.setText(nullToEmpty(m.getValueAt(row, 7)));
        txtTotalRoom.setText(nullToEmpty(m.getValueAt(row, 8)));
        txtDateOfBooking.setText(nullToEmpty(m.getValueAt(row, 9)));
    }

    private String nullToEmpty(Object o) { return o == null ? "" : o.toString(); }

    private void clearFields() {
        for (JTextField f : new JTextField[]{
            txtCusId, txtCustName, txtAddress, txtCity, txtPin,
            txtNoOfPerson, txtIdentProof, txtContNo, txtTotalRoom, txtDateOfBooking}) {
            f.setText("");
        }
        txtCusId.requestFocus();
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.RIGHT);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 17));
        l.setForeground(new Color(0, 51, 51));
        return l;
    }

    private JTextField makeField(int width) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(width, 30));
        return f;
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monotype Corsiva", Font.BOLD, 15));
        b.setBackground(new Color(51, 220, 220));
        b.setForeground(new Color(51, 0, 51));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints lCon() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(6, 8, 6, 8);
        return c;
    }

    private GridBagConstraints fCon() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.anchor = GridBagConstraints.WEST;
        c.fill  = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.insets = new Insets(6, 0, 6, 10);
        return c;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDetail().setVisible(true));
    }
}