import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

public class EmployeeDetail extends JFrame {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/expanses?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tiger";

    private Connection        con = null;
    private ResultSet         rs  = null;
    private PreparedStatement pst;

    // Form fields
    private JTextField txtEmployeeId, txtEmployeeName, txtDOB;
    private JTextField txtCompanyId, txtDOJ, txtSalary;
    private JTextField txtPostName, txtAddress, txtPincode, txtContactNo;
    private JTextField txtHiddenId;   // stores DB primary-key for update/delete

    // Table
    private JTable     table;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton btnSave, btnFind, btnUpdate, btnDelete, btnAddNew, btnHome;

    public EmployeeDetail() {
        initComponents();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Employee Detail");
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
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception ex) {
            Logger.getLogger(EmployeeDetail.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "DB connection failed:\n" + ex.getMessage());
        }
    }

    private void loadTable() {
        try {
            if (con == null || con.isClosed()) connect();
            pst = con.prepareStatement("SELECT * FROM employee");
            rs  = pst.executeQuery();

            // Column names from metadata
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
        JLabel title = new JLabel("EMPLOYEE DETAIL", SwingConstants.CENTER);
        title.setFont(new Font("Monotype Corsiva", Font.BOLD, 30));
        title.setForeground(new Color(51, 51, 51));
        titleRow.add(title, BorderLayout.CENTER);

        btnHome = makeButton("Home");
        JPanel homeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        homeWrap.setOpaque(false);
        homeWrap.add(btnHome);
        titleRow.add(homeWrap, BorderLayout.EAST);
        root.add(titleRow, BorderLayout.NORTH);

        // ── Form panel ──
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 180, 180), 1),
            "Employee Information",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(0, 100, 120)));

        GridBagConstraints lc = lCon();
        GridBagConstraints fc = fCon();

        // Left column
        lc.gridy = 0; fc.gridy = 0;
        formPanel.add(makeLabel("Employee ID :"), lc);
        txtEmployeeId = makeField(160);   formPanel.add(txtEmployeeId, fc);

        lc.gridy = 1; fc.gridy = 1;
        formPanel.add(makeLabel("Employee Name :"), lc);
        txtEmployeeName = makeField(200); formPanel.add(txtEmployeeName, fc);

        lc.gridy = 2; fc.gridy = 2;
        formPanel.add(makeLabel("Date of Birth :"), lc);
        txtDOB = makeField(140);          formPanel.add(txtDOB, fc);

        lc.gridy = 3; fc.gridy = 3;
        formPanel.add(makeLabel("Department ID :"), lc);
        txtCompanyId = makeField(140);    formPanel.add(txtCompanyId, fc);

        lc.gridy = 4; fc.gridy = 4;
        formPanel.add(makeLabel("Hire Date :"), lc);
        txtDOJ = makeField(140);          formPanel.add(txtDOJ, fc);

        lc.gridy = 5; fc.gridy = 5;
        formPanel.add(makeLabel("Salary :"), lc);
        txtSalary = makeField(140);       formPanel.add(txtSalary, fc);

        // Right column (gridx = 2,3)
        GridBagConstraints lc2 = lCon(); lc2.gridx = 2; lc2.insets = new Insets(6,30,6,10);
        GridBagConstraints fc2 = fCon(); fc2.gridx = 3;

        lc2.gridy = 0; fc2.gridy = 0;
        formPanel.add(makeLabel("Role :"), lc2);
        txtPostName = makeField(160);     formPanel.add(txtPostName, fc2);

        lc2.gridy = 1; fc2.gridy = 1;
        formPanel.add(makeLabel("Address :"), lc2);
        txtAddress = makeField(200);      formPanel.add(txtAddress, fc2);

        lc2.gridy = 2; fc2.gridy = 2;
        formPanel.add(makeLabel("Pin Code :"), lc2);
        txtPincode = makeField(120);      formPanel.add(txtPincode, fc2);

        lc2.gridy = 3; fc2.gridy = 3;
        formPanel.add(makeLabel("Contact No. :"), lc2);
        txtContactNo = makeField(160);    formPanel.add(txtContactNo, fc2);

        // Hidden ID field (invisible)
        txtHiddenId = new JTextField();
        txtHiddenId.setVisible(false);
        formPanel.add(txtHiddenId, new GridBagConstraints());

        root.add(formPanel, BorderLayout.CENTER);

        // ── Button row ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnRow.setOpaque(false);
        btnSave   = makeButton("Save");
        btnFind   = makeButton("Find");
        btnUpdate = makeButton("Update");
        btnDelete = makeButton("Delete");
        btnAddNew = makeButton("Add / New");
        btnRow.add(btnSave);
        btnRow.add(btnFind);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);
        btnRow.add(btnAddNew);

        // ── Table ──
        table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.setSelectionBackground(new Color(0, 200, 200));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 220));

        JPanel south = new JPanel(new BorderLayout(0, 4));
        south.setOpaque(false);
        south.add(btnRow, BorderLayout.NORTH);
        south.add(scroll, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        // ── Actions ──
        btnSave.addActionListener(e -> saveRecord());
        btnFind.addActionListener(e -> findRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnAddNew.addActionListener(e -> clearFields());
        btnHome.addActionListener(e -> { new MdiForm().setVisible(true); dispose(); });

        // ── Table row click → populate form ──
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { populateFromTable(); }
        });

        // ── Tab order ──
        JTextField[] order = {
            txtEmployeeId, txtEmployeeName, txtDOB, txtCompanyId,
            txtDOJ, txtSalary, txtPostName, txtAddress, txtPincode, txtContactNo
        };
        KeyListener kl = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_ENTER || code == KeyEvent.VK_TAB)
                    ((Component) e.getSource()).transferFocus();
                else if (code == KeyEvent.VK_UP)
                    ((Component) e.getSource()).transferFocusBackward();
            }
        };
        for (JTextField f : order) {
            f.setFocusTraversalKeysEnabled(false);
            f.addKeyListener(kl);
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────
    private void saveRecord() {
        try {
            pst = con.prepareStatement(
                "INSERT INTO employee(employeeId,employeename,dob,companyId," +
                "dateofjoin,Salary,postName,address,pincode,contactno) VALUES(?,?,?,?,?,?,?,?,?,?)");
            setCommonParams(pst);
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
        String id = txtHiddenId.getText().trim();
        if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE employee SET employeeId=?,employeename=?,dob=?,companyId=?," +
                "dateofjoin=?,Salary=?,postName=?,address=?,pincode=?,contactno=? WHERE id=?");
            setCommonParams(pst);
            pst.setString(11, id);
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
                loadTable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update Error:\n" + ex.getMessage());
        }
    }

    private void findRecord() {
        String eid = JOptionPane.showInputDialog(this, "Enter Employee ID to search:");
        if (eid == null || eid.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM employee WHERE employeeId=?");
            pst.setString(1, eid.trim());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtHiddenId.setText(rs.getString(1));
                txtEmployeeId.setText(rs.getString(2));
                txtEmployeeName.setText(rs.getString(3));
                txtDOB.setText(rs.getString(4));
                txtCompanyId.setText(rs.getString(5));
                txtDOJ.setText(rs.getString(6));
                txtSalary.setText(rs.getString(7));
                txtPostName.setText(rs.getString(8));
                txtAddress.setText(rs.getString(9));
                txtPincode.setText(rs.getString(10));
                txtContactNo.setText(rs.getString(11));
            } else {
                JOptionPane.showMessageDialog(this, "No record found!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Find Error:\n" + ex.getMessage());
        }
    }

    private void deleteRecord() {
        String eid = JOptionPane.showInputDialog(this, "Enter Employee ID to delete:");
        if (eid == null || eid.trim().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete employee: " + eid + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM employee WHERE employeeId=?");
            pst.setString(1, eid.trim());
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

    private void setCommonParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtEmployeeId.getText().trim());
        p.setString(2, txtEmployeeName.getText().trim());
        p.setString(3, txtDOB.getText().trim());
        p.setString(4, txtCompanyId.getText().trim());
        p.setString(5, txtDOJ.getText().trim());
        p.setString(6, txtSalary.getText().trim());
        p.setString(7, txtPostName.getText().trim());
        p.setString(8, txtAddress.getText().trim());
        p.setString(9, txtPincode.getText().trim());
        p.setString(10, txtContactNo.getText().trim());
    }

    private void populateFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        txtHiddenId.setText(m.getValueAt(row, 0).toString());
        txtEmployeeId.setText(m.getValueAt(row, 1).toString());
        txtEmployeeName.setText(m.getValueAt(row, 2).toString());
        txtDOB.setText(m.getValueAt(row, 3).toString());
        txtCompanyId.setText(m.getValueAt(row, 4).toString());
        txtDOJ.setText(m.getValueAt(row, 5).toString());
        txtSalary.setText(m.getValueAt(row, 6).toString());
        txtPostName.setText(m.getValueAt(row, 7).toString());
        txtAddress.setText(m.getValueAt(row, 8).toString());
        txtPincode.setText(m.getValueAt(row, 9).toString());
        txtContactNo.setText(m.getValueAt(row, 10).toString());
    }

    private void clearFields() {
        for (JTextField f : new JTextField[]{
            txtEmployeeId, txtEmployeeName, txtDOB, txtCompanyId,
            txtDOJ, txtSalary, txtPostName, txtAddress, txtPincode,
            txtContactNo, txtHiddenId}) {
            f.setText("");
        }
        txtEmployeeId.requestFocus();
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JLabel makeLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.RIGHT);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 17));
        l.setForeground(new Color(51, 51, 51));
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
        b.setBackground(new Color(0, 220, 220));
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
        SwingUtilities.invokeLater(() -> new EmployeeDetail().setVisible(true));
    }
}