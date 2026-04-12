import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * SalaryDetail – Reimbursements Detail
 * DB table: reimbursement(id, reimbId, claimId, employeeId, processedDate, amount, paymentMode, transactionRef, status)
 */
public class SalaryDetail extends BaseCrudWindow {

    private JTextField txtReimbId, txtClaimId, txtEmployeeId;
    private JTextField txtProcessedDate, txtAmount, txtTransactionRef;
    private JComboBox<String> cmbPaymentMode, cmbStatus;
    private JTextField txtHiddenId;

    public SalaryDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Reimbursements Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM reimbursement");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS reimbursement (" +
                "  id             INT AUTO_INCREMENT PRIMARY KEY," +
                "  reimbId        VARCHAR(20) NOT NULL UNIQUE," +
                "  claimId        VARCHAR(20)," +
                "  employeeId     VARCHAR(20)," +
                "  processedDate  VARCHAR(20)," +
                "  amount         DECIMAL(12,2) DEFAULT 0," +
                "  paymentMode    VARCHAR(30) DEFAULT 'Bank Transfer'," +
                "  transactionRef VARCHAR(50)," +
                "  status         VARCHAR(20) DEFAULT 'Processed'" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("REIMBURSEMENTS DETAIL"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Reimbursement Information");
        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Reimb. ID :"), lc);       txtReimbId      = makeField(150); form.add(txtReimbId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("Claim ID :"), lc);        txtClaimId      = makeField(150); form.add(txtClaimId, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Employee ID :"), lc);     txtEmployeeId   = makeField(150); form.add(txtEmployeeId, fc);
        lc.gridy=3; fc.gridy=3; form.add(makeLabel("Processed Date :"), lc);  txtProcessedDate= makeField(140); form.add(txtProcessedDate, fc);

        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Amount (₹) :"), lc2);  txtAmount       = makeField(150); form.add(txtAmount, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Payment Mode :"), lc2);
        cmbPaymentMode = makeCombo("Bank Transfer", "Cheque", "Cash", "UPI", "NEFT", "RTGS");
        form.add(cmbPaymentMode, fc2);
        lc2.gridy=2; fc2.gridy=2; form.add(makeLabel("Transaction Ref :"), lc2); txtTransactionRef = makeField(180); form.add(txtTransactionRef, fc2);
        lc2.gridy=3; fc2.gridy=3; form.add(makeLabel("Status :"), lc2);
        cmbStatus = makeCombo("Processed", "Pending", "Failed", "On Hold");
        form.add(cmbStatus, fc2);

        txtHiddenId = new JTextField(); txtHiddenId.setVisible(false);
        form.add(txtHiddenId, new GridBagConstraints());

        root.add(form, BorderLayout.CENTER);

        btnSave   = makeButton("Save");
        btnFind   = makeButton("Find");
        btnUpdate = makeButton("Update");
        btnDelete = makeButton("Delete");
        btnNew    = makeButton("Add / New");

        JPanel south = new JPanel(new BorderLayout(0,4));
        south.setOpaque(false);
        south.add(makeButtonRow(btnSave, btnFind, btnUpdate, btnDelete, btnNew), BorderLayout.NORTH);
        south.add(makeTablePane(200), BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveRecord());
        btnFind.addActionListener(e -> findRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnNew.addActionListener(e -> clearFields());
        btnHome.addActionListener(e -> goHome());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { populateFromTable(); }
        });
    }

    private void saveRecord() {
        try {
            pst = con.prepareStatement(
                "INSERT INTO reimbursement(reimbId,claimId,employeeId,processedDate,amount,paymentMode,transactionRef,status)" +
                " VALUES(?,?,?,?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) { showOk("Reimbursement saved!"); clearFields(); loadTable("SELECT * FROM reimbursement"); }
        } catch (SQLException ex) { showErr("Save Error:\n" + ex.getMessage()); }
    }

    private void updateRecord() {
        if (txtHiddenId.getText().trim().isEmpty()) { showErr("Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE reimbursement SET reimbId=?,claimId=?,employeeId=?,processedDate=?,amount=?,paymentMode=?,transactionRef=?,status=? WHERE id=?");
            setParams(pst);
            pst.setString(9, txtHiddenId.getText().trim());
            if (pst.executeUpdate() > 0) { showOk("Reimbursement updated!"); loadTable("SELECT * FROM reimbursement"); }
        } catch (SQLException ex) { showErr("Update Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Reimbursement ID:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM reimbursement WHERE reimbId=?");
            pst.setString(1, id.trim()); rs = pst.executeQuery();
            if (rs.next()) populateFields();
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Reimbursement ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Delete reimbursement: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM reimbursement WHERE reimbId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) { showOk("Deleted!"); clearFields(); loadTable("SELECT * FROM reimbursement"); }
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtReimbId.getText().trim());
        p.setString(2, txtClaimId.getText().trim());
        p.setString(3, txtEmployeeId.getText().trim());
        p.setString(4, txtProcessedDate.getText().trim());
        p.setString(5, txtAmount.getText().trim().isEmpty() ? "0" : txtAmount.getText().trim());
        p.setString(6, cmbPaymentMode.getSelectedItem().toString());
        p.setString(7, txtTransactionRef.getText().trim());
        p.setString(8, cmbStatus.getSelectedItem().toString());
    }

    private void populateFields() throws SQLException {
        txtHiddenId.setText(rs.getString(1));   txtReimbId.setText(rs.getString(2));
        txtClaimId.setText(rs.getString(3));    txtEmployeeId.setText(rs.getString(4));
        txtProcessedDate.setText(rs.getString(5)); txtAmount.setText(rs.getString(6));
        cmbPaymentMode.setSelectedItem(rs.getString(7)); txtTransactionRef.setText(rs.getString(8));
        cmbStatus.setSelectedItem(rs.getString(9));
    }

    private void populateFromTable() {
        int row = table.getSelectedRow(); if (row < 0) return;
        txtHiddenId.setText(cell(row,0));   txtReimbId.setText(cell(row,1));
        txtClaimId.setText(cell(row,2));    txtEmployeeId.setText(cell(row,3));
        txtProcessedDate.setText(cell(row,4)); txtAmount.setText(cell(row,5));
        cmbPaymentMode.setSelectedItem(cell(row,6)); txtTransactionRef.setText(cell(row,7));
        cmbStatus.setSelectedItem(cell(row,8));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtReimbId.setText(""); txtClaimId.setText("");
        txtEmployeeId.setText(""); txtProcessedDate.setText(""); txtAmount.setText("");
        cmbPaymentMode.setSelectedIndex(0); txtTransactionRef.setText(""); cmbStatus.setSelectedIndex(0);
        txtReimbId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalaryDetail().setVisible(true));
    }
}