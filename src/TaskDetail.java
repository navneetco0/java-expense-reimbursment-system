import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * TaskDetail – Audit Log Detail
 * DB table: auditlog(id, logId, userId, action, targetTable, targetId, timestamp, oldValue, newValue, ipAddress)
 * Note: Audit logs are read-only by design. Only delete (purge) and search are allowed.
 */
public class TaskDetail extends BaseCrudWindow {

    private JTextField txtLogId, txtUserId, txtTargetTable;
    private JTextField txtTargetId, txtTimestamp, txtOldValue, txtNewValue, txtIpAddress;
    private JComboBox<String> cmbAction;
    private JTextField txtHiddenId;

    public TaskDetail() {
        initUI();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System – Audit Log Detail");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        connectDB();
        createTableIfNotExists();
        loadTable("SELECT * FROM auditlog ORDER BY id DESC");
    }

    private void createTableIfNotExists() {
        try {
            con.createStatement().executeUpdate(
                "CREATE TABLE IF NOT EXISTS auditlog (" +
                "  id          INT AUTO_INCREMENT PRIMARY KEY," +
                "  logId       VARCHAR(30) NOT NULL UNIQUE," +
                "  userId      VARCHAR(30)," +
                "  action      VARCHAR(20)," +
                "  targetTable VARCHAR(50)," +
                "  targetId    VARCHAR(30)," +
                "  timestamp   VARCHAR(30)," +
                "  oldValue    VARCHAR(255)," +
                "  newValue    VARCHAR(255)," +
                "  ipAddress   VARCHAR(30)" +
                ")");
        } catch (SQLException ex) { showErr("Table init:\n" + ex.getMessage()); }
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBackground(new Color(235, 248, 255));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        setContentPane(root);

        root.add(makeTitlePanel("AUDIT LOG DETAIL"), BorderLayout.NORTH);

        JPanel form = makeTitledForm("Audit Log Entry");
        GridBagConstraints lc = lCon(0), fc = fCon(1);
        GridBagConstraints lc2 = lCon(2), fc2 = fCon(3);

        lc.gridy=0; fc.gridy=0; form.add(makeLabel("Log ID :"), lc);       txtLogId      = makeField(160); form.add(txtLogId, fc);
        lc.gridy=1; fc.gridy=1; form.add(makeLabel("User ID :"), lc);      txtUserId     = makeField(150); form.add(txtUserId, fc);
        lc.gridy=2; fc.gridy=2; form.add(makeLabel("Action :"), lc);
        cmbAction = makeCombo("INSERT", "UPDATE", "DELETE", "LOGIN", "LOGOUT", "VIEW");
        form.add(cmbAction, fc);
        lc.gridy=3; fc.gridy=3; form.add(makeLabel("Target Table :"), lc); txtTargetTable= makeField(150); form.add(txtTargetTable, fc);
        lc.gridy=4; fc.gridy=4; form.add(makeLabel("Target ID :"), lc);    txtTargetId   = makeField(150); form.add(txtTargetId, fc);

        lc2.gridy=0; fc2.gridy=0; form.add(makeLabel("Timestamp :"), lc2);  txtTimestamp  = makeField(180); form.add(txtTimestamp, fc2);
        lc2.gridy=1; fc2.gridy=1; form.add(makeLabel("Old Value :"), lc2);  txtOldValue   = makeField(200); form.add(txtOldValue, fc2);
        lc2.gridy=2; fc2.gridy=2; form.add(makeLabel("New Value :"), lc2);  txtNewValue   = makeField(200); form.add(txtNewValue, fc2);
        lc2.gridy=3; fc2.gridy=3; form.add(makeLabel("IP Address :"), lc2); txtIpAddress  = makeField(150); form.add(txtIpAddress, fc2);

        // Info label – audit log is append-only; update not allowed
        GridBagConstraints ic = new GridBagConstraints();
        ic.gridx=0; ic.gridy=5; ic.gridwidth=4; ic.insets=new Insets(4,10,0,0); ic.anchor=GridBagConstraints.WEST;
        JLabel info = new JLabel("ℹ  Audit logs are append-only. Update is disabled for data integrity.");
        info.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        info.setForeground(new Color(100, 100, 180));
        form.add(info, ic);

        txtHiddenId = new JTextField(); txtHiddenId.setVisible(false);
        form.add(txtHiddenId, new GridBagConstraints());

        root.add(form, BorderLayout.CENTER);

        btnSave   = makeButton("Log Entry");
        btnFind   = makeButton("Search");
        btnDelete = makeButton("Purge Log");
        btnNew    = makeButton("Clear");
        JButton btnRefresh = makeButton("Refresh");

        JPanel south = new JPanel(new BorderLayout(0,4));
        south.setOpaque(false);
        south.add(makeButtonRow(btnSave, btnFind, btnDelete, btnNew, btnRefresh), BorderLayout.NORTH);
        south.add(makeTablePane(200), BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveRecord());
        btnFind.addActionListener(e -> findRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnNew.addActionListener(e -> clearFields());
        btnRefresh.addActionListener(e -> loadTable("SELECT * FROM auditlog ORDER BY id DESC"));
        btnHome.addActionListener(e -> goHome());
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { populateFromTable(); }
        });
    }

    private void saveRecord() {
        // Auto-fill timestamp if blank
        if (txtTimestamp.getText().trim().isEmpty())
            txtTimestamp.setText(new java.util.Date().toString());
        try {
            pst = con.prepareStatement(
                "INSERT INTO auditlog(logId,userId,action,targetTable,targetId,timestamp,oldValue,newValue,ipAddress)" +
                " VALUES(?,?,?,?,?,?,?,?,?)");
            setParams(pst);
            if (pst.executeUpdate() > 0) {
                showOk("Log entry recorded!");
                clearFields();
                loadTable("SELECT * FROM auditlog ORDER BY id DESC");
            }
        } catch (SQLException ex) { showErr("Log Error:\n" + ex.getMessage()); }
    }

    private void findRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Log ID or User ID to search:");
        if (id == null || id.trim().isEmpty()) return;
        try {
            pst = con.prepareStatement(
                "SELECT * FROM auditlog WHERE logId=? OR userId=? ORDER BY id DESC");
            pst.setString(1, id.trim()); pst.setString(2, id.trim());
            rs = pst.executeQuery();
            // Load results into table
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] names = new String[cols];
            for (int i = 0; i < cols; i++) names[i] = meta.getColumnLabel(i + 1);
            tableModel = new DefaultTableModel(names, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            boolean found = false;
            while (rs.next()) {
                found = true;
                Object[] row = new Object[cols];
                for (int i = 0; i < cols; i++) row[i] = rs.getObject(i + 1);
                tableModel.addRow(row);
            }
            table.setModel(tableModel);
            if (!found) showErr("No records found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void deleteRecord() {
        String id = JOptionPane.showInputDialog(this, "Enter Log ID to purge:");
        if (id == null || id.trim().isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this,
                "Purge log entry: " + id + "?\nThis action cannot be undone!",
                "Confirm Purge", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        try {
            pst = con.prepareStatement("DELETE FROM auditlog WHERE logId=?");
            pst.setString(1, id.trim());
            if (pst.executeUpdate() > 0) {
                showOk("Log purged!");
                clearFields();
                loadTable("SELECT * FROM auditlog ORDER BY id DESC");
            } else showErr("Log ID not found!");
        } catch (SQLException ex) { showErr(ex.getMessage()); }
    }

    private void setParams(PreparedStatement p) throws SQLException {
        p.setString(1, txtLogId.getText().trim());
        p.setString(2, txtUserId.getText().trim());
        p.setString(3, cmbAction.getSelectedItem().toString());
        p.setString(4, txtTargetTable.getText().trim());
        p.setString(5, txtTargetId.getText().trim());
        p.setString(6, txtTimestamp.getText().trim());
        p.setString(7, txtOldValue.getText().trim());
        p.setString(8, txtNewValue.getText().trim());
        p.setString(9, txtIpAddress.getText().trim());
    }

    private void populateFromTable() {
        int row = table.getSelectedRow(); if (row < 0) return;
        txtHiddenId.setText(cell(row,0));    txtLogId.setText(cell(row,1));
        txtUserId.setText(cell(row,2));      cmbAction.setSelectedItem(cell(row,3));
        txtTargetTable.setText(cell(row,4)); txtTargetId.setText(cell(row,5));
        txtTimestamp.setText(cell(row,6));   txtOldValue.setText(cell(row,7));
        txtNewValue.setText(cell(row,8));    txtIpAddress.setText(cell(row,9));
    }

    private void clearFields() {
        txtHiddenId.setText(""); txtLogId.setText(""); txtUserId.setText("");
        cmbAction.setSelectedIndex(0); txtTargetTable.setText(""); txtTargetId.setText("");
        txtTimestamp.setText(""); txtOldValue.setText(""); txtNewValue.setText("");
        txtIpAddress.setText(""); txtLogId.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskDetail().setVisible(true));
    }
}