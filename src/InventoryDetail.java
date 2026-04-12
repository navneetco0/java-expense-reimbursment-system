import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * InventoryDetail – Approval Detail
 * DB table: approval(id, approvalId, claimId, approverId, approvalDate, status, remarks, amount)
 */
public class InventoryDetail extends BaseCrudWindow {

    private JTextField txtApprovalId, txtClaimId, txtApproverId;
    private JTextField txtApprovalDate, txtAmount, txtRemarks;
    private JComboBox<String> cmbStatus;
    private JTextField txtHiddenId;

    public InventoryDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Approval Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM approval");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS approval (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  approvalId   VARCHAR(20) NOT NULL UNIQUE," +
                "  claimId      VARCHAR(20)," +
                "  approverId   VARCHAR(20)," +
                "  approvalDate VARCHAR(20)," +
                "  amount       DECIMAL(12,2) DEFAULT 0," +
                "  status       VARCHAR(20) DEFAULT 'Pending'," +
                "  remarks      VARCHAR(255)" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("APPROVAL DETAIL"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Approval Information");
        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Approval ID :"), lc);    txtApprovalId   = makeField(150); form.add(txtApprovalId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("Claim ID :"), lc);       txtClaimId      = makeField(150); form.add(txtClaimId, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Approver ID :"), lc);    txtApproverId   = makeField(150); form.add(txtApproverId, fc);
        lc.gridy=3; fc.gridy=3; form.add(makeLabel("Approval Date :"), lc);  txtApprovalDate = makeField(140); form.add(txtApprovalDate, fc);

        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Amount (₹) :"), lc2); txtAmount  = makeField(150); form.add(txtAmount, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Status :"), lc2);
        cmbStatus = makeCombo("Pending", "Approved", "Rejected", "Partially Approved");
        form.add(cmbStatus, fc2);
        lc2.gridy=2; fc2.gridy=2; form.add(makeLabel("Remarks :"), lc2);    txtRemarks = makeField(220); form.add(txtRemarks, fc2);

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
        south.add(makeTablePane(220), BorderLayout.CENTER);
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
                "INSERT INTO approval(approvalId,claimId,approverId,approvalDate,amount,status,remarks)" +
                " VALUES(?,?,?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) { showOk("Approval saved!"); clearFields(); loadTable("SELECT * FROM approval"); }
        } catch (SQLException ex) { showErr("Save Error:\n" + ex.getMessage()); }
    }

    private void updateRecord() {
        if (txtHiddenId.getText().trim().isEmpty()) { showErr("Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE approval SET approvalId=?,claimId=?,approverId=?,approvalDate=?,amount=?,status=?,remarks=? WHERE id=?");
            setParams(pst);
            pst.setString(8, txtHiddenId.getText().trim());
            if (pst.executeUpdate() > 0) { showOk("Approval updated!"); loadTable("SELECT * FROM approval"); }
        } catch (SQLException ex) { showErr("Update Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Approval ID:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM approval WHERE approvalId=?");
            pst.setString(1, id.trim()); rs = pst.executeQuery();
            if (rs.next()) populateFields();
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Approval ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Delete approval: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM approval WHERE approvalId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) { showOk("Deleted!"); clearFields(); loadTable("SELECT * FROM approval"); }
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtApprovalId.getText().trim());
        p.setString(2, txtClaimId.getText().trim());
        p.setString(3, txtApproverId.getText().trim());
        p.setString(4, txtApprovalDate.getText().trim());
        p.setString(5, txtAmount.getText().trim().isEmpty() ? "0" : txtAmount.getText().trim());
        p.setString(6, cmbStatus.getSelectedItem().toString());
        p.setString(7, txtRemarks.getText().trim());
    }

    private void populateFields() throws SQLException {
        txtHiddenId.setText(rs.getString(1));   txtApprovalId.setText(rs.getString(2));
        txtClaimId.setText(rs.getString(3));    txtApproverId.setText(rs.getString(4));
        txtApprovalDate.setText(rs.getString(5)); txtAmount.setText(rs.getString(6));
        cmbStatus.setSelectedItem(rs.getString(7)); txtRemarks.setText(rs.getString(8));
    }

    private void populateFromTable() {
        int row = table.getSelectedRow(); if (row < 0) return;
        txtHiddenId.setText(cell(row,0));   txtApprovalId.setText(cell(row,1));
        txtClaimId.setText(cell(row,2));    txtApproverId.setText(cell(row,3));
        txtApprovalDate.setText(cell(row,4)); txtAmount.setText(cell(row,5));
        cmbStatus.setSelectedItem(cell(row,6)); txtRemarks.setText(cell(row,7));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtApprovalId.setText(""); txtClaimId.setText("");
        txtApproverId.setText(""); txtApprovalDate.setText(""); txtAmount.setText("");
        cmbStatus.setSelectedIndex(0); txtRemarks.setText("");
        txtApprovalId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryDetail().setVisible(true));
    }
}