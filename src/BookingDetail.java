import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

/**
 * BookingDetail – Managers Detail
 * DB table: manager(managerId, managerName, department, email, phone, hireDate, salary, status)
 */
public class BookingDetail extends BaseCrudWindow {

    private JTextField txtManagerId, txtManagerName, txtDepartment;
    private JTextField txtEmail, txtPhone, txtHireDate, txtSalary;
    private JComboBox<String> cmbStatus;
    private JTextField txtHiddenId;

    public BookingDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Managers Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM manager");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS manager (" +
                "  id          INT AUTO_INCREMENT PRIMARY KEY," +
                "  managerId   VARCHAR(20)  NOT NULL UNIQUE," +
                "  managerName VARCHAR(100) NOT NULL," +
                "  department  VARCHAR(80)," +
                "  email       VARCHAR(100)," +
                "  phone       VARCHAR(20)," +
                "  hireDate    VARCHAR(20)," +
                "  salary      DECIMAL(12,2)," +
                "  status      VARCHAR(20) DEFAULT 'Active'" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("MANAGERS DETAIL"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Manager Information");

        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        // Left column
        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Manager ID :"), lc); txtManagerId   = makeField(150); form.add(txtManagerId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("Manager Name :"), lc); txtManagerName = makeField(190); form.add(txtManagerName, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Department :"), lc); txtDepartment  = makeField(170); form.add(txtDepartment, fc);
        lc.gridy=3; fc.gridy=3; form.add(makeLabel("Hire Date :"), lc); txtHireDate    = makeField(140); form.add(txtHireDate, fc);

        // Right column
        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Email :"), lc2); txtEmail  = makeField(200); form.add(txtEmail, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Phone :"), lc2); txtPhone  = makeField(150); form.add(txtPhone, fc2);
        lc2.gridy=2; fc2.gridy=2; form.add(makeLabel("Salary :"), lc2); txtSalary = makeField(150); form.add(txtSalary, fc2);
        lc2.gridy=3; fc2.gridy=3; form.add(makeLabel("Status :"), lc2);
        cmbStatus = makeCombo("Active", "Inactive", "On Leave");
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
        south.add(makeTablePane(220), BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        // Actions
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
                "INSERT INTO manager(managerId,managerName,department,email,phone,hireDate,salary,status)" +
                " VALUES(?,?,?,?,?,?,?,?)");
            setParams(pst, false);
            if (pst.executeUpdate() > 0) { showOk("Manager saved!"); clearFields(); loadTable("SELECT * FROM manager"); }
        } catch (SQLException ex) { showErr("Save Error:\n" + ex.getMessage()); }
    }

    private void updateRecord() {
        if (txtHiddenId.getText().trim().isEmpty()) { showErr("Select a row first!"); return; }
        try {
            pst = con.prepareStatement(
                "UPDATE manager SET managerId=?,managerName=?,department=?,email=?,phone=?,hireDate=?,salary=?,status=? WHERE id=?");
            setParams(pst, false);
            pst.setString(9, txtHiddenId.getText().trim());
            if (pst.executeUpdate() > 0) { showOk("Manager updated!"); loadTable("SELECT * FROM manager"); }
        } catch (SQLException ex) { showErr("Update Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Manager ID to search:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement("SELECT * FROM manager WHERE managerId=?");
            pst.setString(1, id.trim());
            rs = pst.executeQuery();
            if (rs.next()) populateFields();
            else showErr("No record found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Manager ID to delete:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Delete manager: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM manager WHERE managerId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) { showOk("Deleted!"); clearFields(); loadTable("SELECT * FROM manager"); }
            else showErr("Not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p, boolean withId) throws SQLException {
        p.setString(1, txtManagerId.getText().trim());
        p.setString(2, txtManagerName.getText().trim());
        p.setString(3, txtDepartment.getText().trim());
        p.setString(4, txtEmail.getText().trim());
        p.setString(5, txtPhone.getText().trim());
        p.setString(6, txtHireDate.getText().trim());
        p.setString(7, txtSalary.getText().trim().isEmpty() ? "0" : txtSalary.getText().trim());
        p.setString(8, cmbStatus.getSelectedItem().toString());
    }

    private void populateFields() throws SQLException {
        txtHiddenId.setText(rs.getString(1));
        txtManagerId.setText(rs.getString(2));
        txtManagerName.setText(rs.getString(3));
        txtDepartment.setText(rs.getString(4));
        txtEmail.setText(rs.getString(5));
        txtPhone.setText(rs.getString(6));
        txtHireDate.setText(rs.getString(7));
        txtSalary.setText(rs.getString(8));
        cmbStatus.setSelectedItem(rs.getString(9));
    }

    private void populateFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtHiddenId.setText(cell(row,0));
        txtManagerId.setText(cell(row,1));
        txtManagerName.setText(cell(row,2));
        txtDepartment.setText(cell(row,3));
        txtEmail.setText(cell(row,4));
        txtPhone.setText(cell(row,5));
        txtHireDate.setText(cell(row,6));
        txtSalary.setText(cell(row,7));
        cmbStatus.setSelectedItem(cell(row,8));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtManagerId.setText(""); txtManagerName.setText("");
        txtDepartment.setText(""); txtEmail.setText(""); txtPhone.setText("");
        txtHireDate.setText(""); txtSalary.setText(""); cmbStatus.setSelectedIndex(0);
        txtManagerId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingDetail().setVisible(true));
    }
}