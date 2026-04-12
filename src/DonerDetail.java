import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * DonerDetail – Expense Categories Detail
 * DB table: expensecategory(id, categoryId, categoryName, description, budgetLimit, isActive)
 */
public class DonerDetail extends BaseCrudWindow {

    private JTextField    txtCategoryId, txtCategoryName, txtDescription, txtBudgetLimit;
    private JComboBox<String> cmbIsActive;
    private JTextField    txtHiddenId;

    public DonerDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Expense Categories");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM expensecategory");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS expensecategory (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  categoryId   VARCHAR(20) NOT NULL UNIQUE," +
                "  categoryName VARCHAR(100) NOT NULL," +
                "  description  VARCHAR(255)," +
                "  budgetLimit  DECIMAL(12,2) DEFAULT 0," +
                "  isActive     VARCHAR(10) DEFAULT 'Yes'" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("EXPENSE CATEGORIES"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Category Information");
        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Category ID :"), lc);   txtCategoryId   = makeField(150); form.add(txtCategoryId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("Category Name :"), lc); txtCategoryName = makeField(200); form.add(txtCategoryName, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Description :"), lc);   txtDescription  = makeField(250); form.add(txtDescription, fc);

        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Budget Limit :"), lc2); txtBudgetLimit = makeField(150); form.add(txtBudgetLimit, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Is Active :"), lc2);
        cmbIsActive = makeCombo("Yes", "No");
        form.add(cmbIsActive, fc2);

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
                "INSERT INTO expensecategory(categoryId,categoryName,description,budgetLimit,isActive) VALUES(?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) { showOk("Category saved!"); clearFields(); loadTable("SELECT * FROM expensecategory"); }
        } catch (SQLException ex) { showErr("Save Error:\n" + ex.getMessage()); }
    }

    private void updateRecord() {
        if (txtHiddenId.getText().trim().isEmpty()) { showErr("Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE expensecategory SET categoryId=?,categoryName=?,description=?,budgetLimit=?,isActive=? WHERE id=?");
            setParams(pst);
            pst.setString(6, txtHiddenId.getText().trim());
            if (pst.executeUpdate() > 0) { showOk("Category updated!"); loadTable("SELECT * FROM expensecategory"); }
        } catch (SQLException ex) { showErr("Update Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Category ID:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM expensecategory WHERE categoryId=?");
            pst.setString(1, id.trim()); rs = pst.executeQuery();
            if (rs.next()) populateFields();
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Category ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Delete category: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM expensecategory WHERE categoryId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) { showOk("Deleted!"); clearFields(); loadTable("SELECT * FROM expensecategory"); }
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtCategoryId.getText().trim());
        p.setString(2, txtCategoryName.getText().trim());
        p.setString(3, txtDescription.getText().trim());
        p.setString(4, txtBudgetLimit.getText().trim().isEmpty() ? "0" : txtBudgetLimit.getText().trim());
        p.setString(5, cmbIsActive.getSelectedItem().toString());
    }

    private void populateFields() throws SQLException {
        txtHiddenId.setText(rs.getString(1));
        txtCategoryId.setText(rs.getString(2));
        txtCategoryName.setText(rs.getString(3));
        txtDescription.setText(rs.getString(4));
        txtBudgetLimit.setText(rs.getString(5));
        cmbIsActive.setSelectedItem(rs.getString(6));
    }

    private void populateFromTable() {
        int row = table.getSelectedRow(); if (row < 0) return;
        txtHiddenId.setText(cell(row,0));   txtCategoryId.setText(cell(row,1));
        txtCategoryName.setText(cell(row,2)); txtDescription.setText(cell(row,3));
        txtBudgetLimit.setText(cell(row,4)); cmbIsActive.setSelectedItem(cell(row,5));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtCategoryId.setText(""); txtCategoryName.setText("");
        txtDescription.setText(""); txtBudgetLimit.setText(""); cmbIsActive.setSelectedIndex(0);
        txtCategoryId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DonerDetail().setVisible(true));
    }
}