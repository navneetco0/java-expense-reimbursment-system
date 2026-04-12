import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Transaction Detail Form - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class TransactionDetail extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    // --- Components ---
    private JLabel  jLabel1, jLabel2, jLabel3, jLabel4, jLabel5,
                    jLabel6, jLabel7, jLabel8, jLabel11;
    private JTextField TransactionIdtxt, firstNametxt, lastnametxt,
                       emailtxt, phonetxt, addresstxt, createdAttxt, idtxt;
    private JButton jButton1, jButton2, jButton3, jButton4, jButton5, jButton6;
    private JTable  jTable1;
    private JScrollPane jScrollPane1;

    public TransactionDetail() {
        initComponents();
        this.setTitle("Expense Tracker and Budget Management");
        // AppMenuBar.createMenu(this) -- wire up when AppMenuBar is available
        this.setLocation(200, 200);
        this.setSize(1200, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        idtxt.setVisible(false);
        Connect();
        showTableData();
        this.setVisible(true);
    }

    // ------------------------------------------------------------------ DB --
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expansebudget?useSSL=false",
                    "RAJANISH", "RAJANISH");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(TransactionDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Replaces DbUtils.resultSetToTableModel — pure JDBC */
    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Vector<String> colNames = new Vector<>();
        for (int i = 1; i <= cols; i++) colNames.add(meta.getColumnLabel(i));
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
            data.add(row);
        }
        return new DefaultTableModel(data, colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public void showTableData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expansebudget?useSSL=false",
                    "RAJANISH", "RAJANISH");
            pst = con.prepareStatement("SELECT * FROM RecurringExpanse");
            rs  = pst.executeQuery();
            jTable1.setModel(buildTableModel(rs));
            // Left-align header
            DefaultTableCellRenderer hr = (DefaultTableCellRenderer)
                    jTable1.getTableHeader().getDefaultRenderer();
            hr.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(TransactionDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        // Use null layout (replaces AbsoluteLayout)
        getContentPane().setLayout(null);
        setFont(new Font("Arial", Font.BOLD, 12));

        // --- Labels ---
        jLabel2 = makeLabel(" Transaction-Detail", "Monotype Corsiva", 28);
        jLabel2.setBounds(270, 10, 300, 49);

        jLabel1 = makeLabel("Transaction-Id ::", "Monotype Corsiva", 18);
        jLabel1.setBounds(110, 90, 190, 45);

        jLabel3 = makeLabel("Transaction-Type ::", "Monotype Corsiva", 18);
        jLabel3.setBounds(110, 140, 210, 43);

        jLabel4 = makeLabel("Category-Name ::", "Monotype Corsiva", 18);
        jLabel4.setBounds(110, 190, 190, 40);

        jLabel5 = makeLabel("Email ::", "Monotype Corsiva", 18);
        jLabel5.setBounds(110, 240, 190, 40);

        jLabel11 = makeLabel("User-Id ::", "Monotype Corsiva", 18);
        jLabel11.setBounds(107, 291, 200, 44);

        jLabel7 = makeLabel("Description ::", "Monotype Corsiva", 18);
        jLabel7.setBounds(520, 90, 180, 46);

        jLabel8 = makeLabel("Created-Date ::", "Monotype Corsiva", 18);
        jLabel8.setBounds(520, 140, 180, 44);

        // Decorative background label (no image in VS Code env — just a placeholder)
        jLabel6 = new JLabel();
        jLabel6.setBounds(0, 0, 1200, 650);

        // --- Text Fields ---
        TransactionIdtxt = makeField(310, 90, 204, 45);
        firstNametxt     = makeField(310, 140, 204, 43);
        lastnametxt      = makeField(310, 190, 204, 39);
        emailtxt         = makeField(310, 240, 204, 40);
        phonetxt         = makeField(310, 290, 204, 45);
        addresstxt       = makeField(710, 90, 200, 40);
        createdAttxt     = makeField(710, 140, 200, 40);
        idtxt            = makeField(940, 320, 110, 30);

        // --- Buttons ---
        jButton1 = makeButton("Add/New",  530, 320, 110, 31);
        jButton2 = makeButton("Save",     640, 320, 90,  31);
        jButton3 = makeButton("Update",   730, 320, 100, 31);
        jButton4 = makeButton("Delete",   830, 320, 110, 31);
        jButton5 = makeButton("Mdi-Form", 710, 20,  110, 30);
        jButton6 = makeButton("Find",     840, 20,  80,  30);

        // --- Table ---
        jTable1 = new JTable();
        jTable1.setFont(new Font("Arial Black", Font.BOLD, 14));
        jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBounds(80, 360, 940, 230);

        // Add in correct Z-order (background last so it doesn't cover controls)
        for (Component c : new Component[]{
            jLabel2, jLabel1, jLabel3, jLabel4, jLabel5, jLabel11, jLabel7, jLabel8,
            TransactionIdtxt, firstNametxt, lastnametxt, emailtxt, phonetxt,
            addresstxt, createdAttxt, idtxt,
            jButton1, jButton2, jButton3, jButton4, jButton5, jButton6,
            jScrollPane1
        }) getContentPane().add(c);

        // ------ Listeners ------
        jButton1.addActionListener(e -> jButton1ActionPerformed());
        jButton2.addActionListener(e -> jButton2ActionPerformed());
        jButton3.addActionListener(e -> jButton3ActionPerformed());
        jButton4.addActionListener(e -> jButton4ActionPerformed());
        jButton5.addActionListener(e -> jButton5ActionPerformed());
        jButton6.addActionListener(e -> jButton6ActionPerformed());

        jTable1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { jTable1MouseClicked(); }
        });

        // Key traversal
        addTabKey(TransactionIdtxt, firstNametxt, null);
        addTabKey(firstNametxt,     lastnametxt,  TransactionIdtxt);
        addTabKey(lastnametxt,      emailtxt,     firstNametxt);
        addTabKey(emailtxt,         phonetxt,     lastnametxt);
        addTabKey(phonetxt,         addresstxt,   emailtxt);
        addTabKey(addresstxt,       createdAttxt, phonetxt);
        addTabKey(createdAttxt,     null,         addresstxt);
    }

    // ----------------------------------------------------- Helpers ----------
    private JLabel makeLabel(String text, String fontName, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(fontName, Font.BOLD, size));
        return l;
    }

    private JTextField makeField(int x, int y, int w, int h) {
        JTextField f = new JTextField();
        f.setFont(new Font("Arial Black", Font.BOLD, 14));
        f.setBounds(x, y, w, h);
        return f;
    }

    private JButton makeButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monotype Corsiva", Font.BOLD, 18));
        b.setForeground(new Color(51, 0, 51));
        b.setBounds(x, y, w, h);
        return b;
    }

    /** Tab / up-arrow key navigation between fields */
    private void addTabKey(JTextField from, JTextField next, JTextField prev) {
        from.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                if (next != null && (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_ENTER || k == KeyEvent.VK_TAB))
                    next.requestFocus();
                if (prev != null && k == KeyEvent.VK_UP)
                    prev.requestFocus();
                idtxt.setVisible(false);
            }
        });
    }

    private void clearFields() {
        TransactionIdtxt.setText(""); firstNametxt.setText("");
        lastnametxt.setText(""); emailtxt.setText("");
        phonetxt.setText(""); addresstxt.setText(""); createdAttxt.setText("");
    }

    // --------------------------------------------------- Button Actions -----
    private void jButton1ActionPerformed() {   // Add/New
        clearFields();
        TransactionIdtxt.requestFocus();
    }

    private void jButton2ActionPerformed() {   // Save
        String id        = TransactionIdtxt.getText().trim();
        String firstName = firstNametxt.getText().trim();
        String lastName  = lastnametxt.getText().trim();
        String email     = emailtxt.getText().trim();
        String phone     = phonetxt.getText().trim();
        String address   = addresstxt.getText().trim();
        String createdAt = createdAttxt.getText().trim();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
            email.isEmpty() || phone.isEmpty() || address.isEmpty() || createdAt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields."); return;
        }
        if (!id.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Transaction ID must be numeric.");
            TransactionIdtxt.requestFocus(); return;
        }
        if (!firstName.matches("[A-Za-z]+")) {
            JOptionPane.showMessageDialog(this, "First name must contain only letters.");
            firstNametxt.requestFocus(); return;
        }
        if (!lastName.matches("[A-Za-z]+")) {
            JOptionPane.showMessageDialog(this, "Last name must contain only letters.");
            lastnametxt.requestFocus(); return;
        }
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            emailtxt.requestFocus(); return;
        }
        if (!isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit phone number.");
            phonetxt.requestFocus(); return;
        }
        if (!isValidDate(createdAt)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (yyyy-MM-dd).");
            createdAttxt.requestFocus(); return;
        }
        try {
            pst = con.prepareStatement(
                "INSERT INTO RecurringExpanse(RecurringExpanseID, FirstName, LastName, " +
                "Email, PhoneNumber, Address, CreatedAt) VALUES (?,?,?,?,?,?,?)");
            pst.setInt(1, Integer.parseInt(id));
            pst.setString(2, firstName); pst.setString(3, lastName);
            pst.setString(4, email);     pst.setString(5, phone);
            pst.setString(6, address);
            pst.setDate(7, java.sql.Date.valueOf(createdAt));
            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Record inserted successfully.");
                clearFields(); TransactionIdtxt.requestFocus();
                showTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert record.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void jButton3ActionPerformed() {   // Update
        if (idtxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a row from the table first."); return;
        }
        try {
            pst = con.prepareStatement(
                "UPDATE RecurringExpanse SET RecurringExpanseID=?, FirstName=?, LastName=?, " +
                "Email=?, PhoneNumber=?, Address=?, CreatedAt=? WHERE id=?");
            pst.setString(1, TransactionIdtxt.getText());
            pst.setString(2, firstNametxt.getText());
            pst.setString(3, lastnametxt.getText());
            pst.setString(4, emailtxt.getText());
            pst.setString(5, phonetxt.getText());
            pst.setString(6, addresstxt.getText());
            pst.setString(7, createdAttxt.getText());
            pst.setString(8, idtxt.getText());
            pst.executeUpdate();
            showTableData();
            JOptionPane.showMessageDialog(this, "Record Updated Successfully!");
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jButton4ActionPerformed() {   // Delete
        String del = JOptionPane.showInputDialog(this, "Enter Transaction Id to Delete:");
        if (del == null) return;
        try {
            pst = con.prepareStatement(
                "DELETE FROM RecurringExpanse WHERE RecurringExpanseID=?");
            pst.setString(1, del);
            int upd = pst.executeUpdate();
            JOptionPane.showMessageDialog(this,
                upd > 0 ? "Record Deleted Successfully!" : "Record Not Found!");
            clearFields();
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        showTableData();
    }

    private void jButton5ActionPerformed() {   // Mdi-Form
        // MdiForm mdiForm = new MdiForm(); mdiForm.show(); this.dispose();
        this.dispose();
    }

    private void jButton6ActionPerformed() {   // Find
        String st = JOptionPane.showInputDialog(this, "Enter Transaction Id to Search:");
        if (st == null) return;
        try {
            pst = con.prepareStatement(
                "SELECT * FROM RecurringExpanse WHERE RecurringExpanseID=?");
            pst.setString(1, st);
            rs = pst.executeQuery();
            if (rs.next()) {
                TransactionIdtxt.setText(rs.getString(2));
                firstNametxt.setText(rs.getString(3));
                lastnametxt.setText(rs.getString(4));
                emailtxt.setText(rs.getString(5));
                phonetxt.setText(rs.getString(6));
                addresstxt.setText(rs.getString(7));
                createdAttxt.setText(rs.getString(8));
                idtxt.setText(rs.getString(1));
                idtxt.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "No Record Found! Try again.");
                clearFields(); TransactionIdtxt.requestFocus();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jTable1MouseClicked() {
        int row = jTable1.getSelectedRow();
        if (row < 0) return;
        DefaultTableModel m = (DefaultTableModel) jTable1.getModel();
        idtxt.setText(m.getValueAt(row, 0).toString());
        TransactionIdtxt.setText(m.getValueAt(row, 1).toString());
        firstNametxt.setText(m.getValueAt(row, 2).toString());
        lastnametxt.setText(m.getValueAt(row, 3).toString());
        emailtxt.setText(m.getValueAt(row, 4).toString());
        phonetxt.setText(m.getValueAt(row, 5).toString());
        addresstxt.setText(m.getValueAt(row, 6).toString());
        createdAttxt.setText(m.getValueAt(row, 7).toString());
        idtxt.setVisible(false);
    }

    // --------------------------------------------------- Validators ---------
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{10}");
    }
    private boolean isValidDate(String date) {
        try { java.sql.Date.valueOf(date); return true; }
        catch (IllegalArgumentException e) { return false; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransactionDetail::new);
    }
}