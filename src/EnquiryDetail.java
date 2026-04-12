import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * EnquiryDetail – Claims Detail
 * DB table: claim(id, claimId, employeeId, categoryId, claimDate, amount, description, status, approvedBy)
 */
public class EnquiryDetail extends BaseCrudWindow {

    private JTextField txtClaimId, txtEmployeeId, txtCategoryId;
    private JTextField txtClaimDate, txtAmount, txtDescription, txtApprovedBy;
    private JComboBox<String> cmbStatus;
    private JTextField txtHiddenId;

    public EnquiryDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Claims Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM claim");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS claim (" +
                "  id          INT AUTO_INCREMENT PRIMARY KEY," +
                "  claimId     VARCHAR(20) NOT NULL UNIQUE," +
                "  employeeId  VARCHAR(20)," +
                "  categoryId  VARCHAR(20)," +
                "  claimDate   VARCHAR(20)," +
                "  amount      DECIMAL(12,2) DEFAULT 0," +
                "  description VARCHAR(255)," +
                "  status      VARCHAR(20) DEFAULT 'Pending'," +
                "  approvedBy  VARCHAR(50)" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("CLAIMS DETAIL"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Claim Information");
        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Claim ID :"), lc);       txtClaimId    = makeField(150); form.add(txtClaimId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("Employee ID :"), lc);    txtEmployeeId = makeField(150); form.add(txtEmployeeId, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Category ID :"), lc);    txtCategoryId = makeField(150); form.add(txtCategoryId, fc);
        lc.gridy=3; fc.gridy=3; form.add(makeLabel("Claim Date :"), lc);     txtClaimDate  = makeField(140); form.add(txtClaimDate, fc);

        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Amount (₹) :"), lc2); txtAmount      = makeField(150); form.add(txtAmount, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Description :"), lc2); txtDescription = makeField(220); form.add(txtDescription, fc2);
        lc2.gridy=2; fc2.gridy=2; form.add(makeLabel("Status :"), lc2);
        cmbStatus = makeCombo("Pending", "Approved", "Rejected", "Under Review");
        form.add(cmbStatus, fc2);
        lc2.gridy=3; fc2.gridy=3; form.add(makeLabel("Approved By :"), lc2); txtApprovedBy = makeField(170); form.add(txtApprovedBy, fc2);

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
                "INSERT INTO claim(claimId,employeeId,categoryId,claimDate,amount,description,status,approvedBy)" +
                " VALUES(?,?,?,?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) { showOk("Claim saved!"); clearFields(); loadTable("SELECT * FROM claim"); }
        } catch (SQLException ex) { showErr("Save Error:\n" + ex.getMessage()); }
    }

    private void updateRecord() {
        if (txtHiddenId.getText().trim().isEmpty()) { showErr("Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE claim SET claimId=?,employeeId=?,categoryId=?,claimDate=?,amount=?,description=?,status=?,approvedBy=? WHERE id=?");
            setParams(pst);
            pst.setString(9, txtHiddenId.getText().trim());
            if (pst.executeUpdate() > 0) { showOk("Claim updated!"); loadTable("SELECT * FROM claim"); }
        } catch (SQLException ex) { showErr("Update Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Claim ID:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM claim WHERE claimId=?");
            pst.setString(1, id.trim()); rs = pst.executeQuery();
            if (rs.next()) populateFields();
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Claim ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Delete claim: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM claim WHERE claimId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) { showOk("Deleted!"); clearFields(); loadTable("SELECT * FROM claim"); }
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtClaimId.getText().trim());
        p.setString(2, txtEmployeeId.getText().trim());
        p.setString(3, txtCategoryId.getText().trim());
        p.setString(4, txtClaimDate.getText().trim());
        p.setString(5, txtAmount.getText().trim().isEmpty() ? "0" : txtAmount.getText().trim());
        p.setString(6, txtDescription.getText().trim());
        p.setString(7, cmbStatus.getSelectedItem().toString());
        p.setString(8, txtApprovedBy.getText().trim());
    }

    private void populateFields() throws SQLException {
        txtHiddenId.setText(rs.getString(1));   txtClaimId.setText(rs.getString(2));
        txtEmployeeId.setText(rs.getString(3)); txtCategoryId.setText(rs.getString(4));
        txtClaimDate.setText(rs.getString(5));  txtAmount.setText(rs.getString(6));
        txtDescription.setText(rs.getString(7)); cmbStatus.setSelectedItem(rs.getString(8));
        txtApprovedBy.setText(rs.getString(9));
    }

    private void populateFromTable() {
        int row = table.getSelectedRow(); if (row < 0) return;
        txtHiddenId.setText(cell(row,0));   txtClaimId.setText(cell(row,1));
        txtEmployeeId.setText(cell(row,2)); txtCategoryId.setText(cell(row,3));
        txtClaimDate.setText(cell(row,4));  txtAmount.setText(cell(row,5));
        txtDescription.setText(cell(row,6)); cmbStatus.setSelectedItem(cell(row,7));
        txtApprovedBy.setText(cell(row,8));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtClaimId.setText(""); txtEmployeeId.setText("");
        txtCategoryId.setText(""); txtClaimDate.setText(""); txtAmount.setText("");
        txtDescription.setText(""); cmbStatus.setSelectedIndex(0); txtApprovedBy.setText("");
        txtClaimId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnquiryDetail().setVisible(true));
    }
}