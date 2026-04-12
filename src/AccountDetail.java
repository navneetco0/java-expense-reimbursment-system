import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Account Detail Form - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class AccountDetail extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    // --- Components ---
    private JLabel  jLabel1, jLabel2, jLabel3, jLabel4, jLabel5,
                    jLabel6, jLabel7, jLabel8, jLabel11, jLabel12;
    private JTextField bnkTxt, grossTxt, accontNoTxt, natureAccountTxt,
                       empIdTxt, dateOpen, empNameTxt, idtxt;
    private JButton jButton1, jButton2, jButton3, jButton4, jButton5, jButton6;
    private JTable  jTable1;
    private JScrollPane jScrollPane1;

    public AccountDetail() {
        initComponents();
        // this.setJMenuBar(AppMenuBar.createMenu(this));
        this.setLocation(200, 200);
        this.setSize(1200, 650);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Connect();
        showTableData();
        idtxt.setVisible(false);
        this.setVisible(true);
    }

    // ------------------------------------------------------------------ DB --
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
            pst = con.prepareStatement("SELECT * FROM account_detail");
            rs  = pst.executeQuery();
            jTable1.setModel(buildTableModel(rs));
            DefaultTableCellRenderer hr = (DefaultTableCellRenderer)
                    jTable1.getTableHeader().getDefaultRenderer();
            hr.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        setTitle("Account-Detail");
        getContentPane().setLayout(null);

        // --- Labels ---
        jLabel1 = makeLabel(" Account-Detail", "Monotype Corsiva", Font.BOLD | Font.ITALIC, 36);
        jLabel1.setForeground(new Color(0, 51, 51));
        jLabel1.setBounds(380, 30, 310, 50);

        jLabel2  = makeLabel("Bank Name :",        "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel2.setBounds(180, 90, 160, 36);
        jLabel3  = makeLabel("Account. No. :",     "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel3.setBounds(180, 140, 170, 35);
        jLabel4  = makeLabel("Bill-Id :",           "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel4.setBounds(180, 190, 160, 32);
        jLabel5  = makeLabel("Customer-Name :",    "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel5.setBounds(150, 240, 200, 38);
        jLabel6  = makeLabel("Total Amount :",     "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel6.setBounds(170, 290, 175, 40);
        jLabel7  = makeLabel("Nature of Account :","Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel7.setBounds(150, 340, 200, 35);
        jLabel8  = makeLabel("Payment-Date :",     "Monotype Corsiva", Font.BOLD | Font.ITALIC, 22); jLabel8.setBounds(150, 390, 200, 33);

        jLabel11 = makeLabel("Develop By::Rajeev Ranjan Pathak", "Monospaced", Font.BOLD, 12);
        jLabel11.setForeground(new Color(0, 51, 51)); jLabel11.setBounds(540, 470, 270, 24);
        jLabel12 = makeLabel("Enroll : 2250951481", "Monospaced", Font.BOLD, 12);
        jLabel12.setForeground(new Color(51, 0, 51)); jLabel12.setBounds(570, 500, 210, 20);

        // --- Text Fields ---
        bnkTxt          = makeField(350, 90,  160, 38);
        accontNoTxt     = makeField(350, 140, 160, 38);
        empIdTxt        = makeField(350, 190, 160, 36);
        empNameTxt      = makeField(350, 240, 160, 38);
        grossTxt        = makeField(350, 290, 160, 38);
        natureAccountTxt= makeField(350, 340, 160, 38);
        dateOpen        = makeField(350, 390, 160, 38);
        idtxt           = makeField(760, 130, 120, 30);

        // --- Buttons ---
        jButton6 = makeButton("Add/New",  540, 130, 110, 38);
        jButton1 = makeButton("Save",     650, 130,  80, 38);
        jButton3 = makeButton("Find",     730, 130,  90, 38);
        jButton4 = makeButton("Delete",   820, 130, 110, 38);
        jButton2 = makeButton("Update",   930, 130, 110, 38);
        jButton5 = makeButton("Home",    1040, 130, 110, 38);

        // --- Table ---
        jTable1 = new JTable();
        jTable1.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBounds(530, 185, 640, 270);

        // Add components
        for (Component c : new Component[]{
            jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8,
            jLabel11, jLabel12,
            bnkTxt, accontNoTxt, empIdTxt, empNameTxt, grossTxt, natureAccountTxt,
            dateOpen, idtxt,
            jButton1, jButton2, jButton3, jButton4, jButton5, jButton6,
            jScrollPane1
        }) getContentPane().add(c);

        // --- Button Listeners ---
        jButton6.addActionListener(e -> clearFields());
        jButton1.addActionListener(e -> saveRecord());
        jButton2.addActionListener(e -> updateRecord());
        jButton3.addActionListener(e -> findRecord());
        jButton4.addActionListener(e -> deleteRecord());
        jButton5.addActionListener(e -> { this.dispose(); });

        jTable1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { rowSelected(); }
        });

        // Key navigation
        addNav(bnkTxt,           accontNoTxt,      null);
        addNav(accontNoTxt,      empIdTxt,          bnkTxt);
        addNav(empIdTxt,         empNameTxt,        accontNoTxt);
        addNav(empNameTxt,       grossTxt,          empIdTxt);
        addNav(grossTxt,         natureAccountTxt,  empNameTxt);
        addNav(natureAccountTxt, dateOpen,          grossTxt);
        addNav(dateOpen,         null,              natureAccountTxt);
    }

    // ----------------------------------------------------- Helpers ----------
    private JLabel makeLabel(String text, String font, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(font, style, size));
        return l;
    }
    private JTextField makeField(int x, int y, int w, int h) {
        JTextField f = new JTextField();
        f.setFont(new Font("ITC Bookman Light", Font.BOLD, 14));
        f.setBounds(x, y, w, h); return f;
    }
    private JButton makeButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monotype Corsiva", Font.BOLD, 16));
        b.setBounds(x, y, w, h); return b;
    }
    private void addNav(JTextField from, JTextField next, JTextField prev) {
        from.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (next != null && (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER))
                    next.requestFocus();
                if (prev != null && e.getKeyCode() == KeyEvent.VK_UP)
                    prev.requestFocus();
            }
        });
    }
    private void clearFields() {
        bnkTxt.setText(""); grossTxt.setText(""); accontNoTxt.setText("");
        natureAccountTxt.setText(""); empIdTxt.setText("");
        dateOpen.setText(""); empNameTxt.setText(""); bnkTxt.requestFocus();
    }

    // --------------------------------------------------- Actions ------------
    private void saveRecord() {
        try {
            pst = con.prepareStatement(
                "INSERT INTO account_detail(bankname, accountno, employeeid, employeename, " +
                "grosssal, natureofacount, dateofopen) VALUES (?,?,?,?,?,?,?)");
            pst.setString(1, bnkTxt.getText());          pst.setString(2, accontNoTxt.getText());
            pst.setString(3, empIdTxt.getText());        pst.setString(4, empNameTxt.getText());
            pst.setString(5, grossTxt.getText());        pst.setString(6, natureAccountTxt.getText());
            pst.setString(7, dateOpen.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record Inserted Successfully");
            clearFields();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        showTableData();
    }

    private void updateRecord() {
        if (idtxt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a row from the table first."); return;
        }
        try {
            pst = con.prepareStatement(
                "UPDATE account_detail SET bankname=?, accountno=?, employeeid=?, " +
                "employeename=?, grosssal=?, natureofacount=?, dateofopen=? WHERE id=?");
            pst.setString(1, bnkTxt.getText());          pst.setString(2, accontNoTxt.getText());
            pst.setString(3, empIdTxt.getText());        pst.setString(4, empNameTxt.getText());
            pst.setString(5, grossTxt.getText());        pst.setString(6, natureAccountTxt.getText());
            pst.setString(7, dateOpen.getText());        pst.setString(8, idtxt.getText());
            pst.executeUpdate();
            showTableData();
            JOptionPane.showMessageDialog(this, "Record Updated!");
        } catch (SQLException ex) {
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void findRecord() {
        String st = JOptionPane.showInputDialog(this, "Enter Account No. to Search:");
        if (st == null) return;
        try {
            pst = con.prepareStatement("SELECT * FROM account_detail WHERE accountno=?");
            pst.setString(1, st); rs = pst.executeQuery();
            if (rs.next()) {
                bnkTxt.setText(rs.getString(2));          accontNoTxt.setText(rs.getString(3));
                empIdTxt.setText(rs.getString(4));        empNameTxt.setText(rs.getString(5));
                grossTxt.setText(rs.getString(6));        natureAccountTxt.setText(rs.getString(7));
                dateOpen.setText(rs.getString(8));        idtxt.setText(rs.getString(1));
            } else {
                JOptionPane.showMessageDialog(this, "No Record Found!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteRecord() {
        String del = JOptionPane.showInputDialog(this, "Enter Account No. to Delete:");
        if (del == null) return;
        try {
            pst = con.prepareStatement("DELETE FROM account_detail WHERE accountno=?");
            pst.setString(1, del);
            int upd = pst.executeUpdate();
            JOptionPane.showMessageDialog(this,
                upd > 0 ? "Record Deleted Successfully!" : "Record Not Found!");
            clearFields();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
        showTableData();
    }

    private void rowSelected() {
        int row = jTable1.getSelectedRow();
        if (row < 0) return;
        DefaultTableModel m = (DefaultTableModel) jTable1.getModel();
        idtxt.setText(m.getValueAt(row, 0).toString());
        bnkTxt.setText(m.getValueAt(row, 1).toString());
        accontNoTxt.setText(m.getValueAt(row, 2).toString());
        empIdTxt.setText(m.getValueAt(row, 3).toString());
        empNameTxt.setText(m.getValueAt(row, 4).toString());
        grossTxt.setText(m.getValueAt(row, 5).toString());
        natureAccountTxt.setText(m.getValueAt(row, 6).toString());
        dateOpen.setText(m.getValueAt(row, 7).toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountDetail::new);
    }
}