import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Account Section Query - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class AccountSectionQuerry extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    private JLabel       jLabel1, jLabel2;
    private JRadioButton jRadioButton1, jRadioButton2, jRadioButton3;
    private JTable       jTable1;
    private JScrollPane  jScrollPane1;
    private JButton      jButton1;

    public AccountSectionQuerry() {
        initComponents();
        // this.setJMenuBar(AppMenuBar.createMenu(this));
        this.setLocation(200, 200);
        this.setSize(1200, 650);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Connect();
        loadTable("SELECT * FROM account_detail", null);
        jRadioButton1.setSelected(true);
        this.setVisible(true);
    }

    // ------------------------------------------------------------------ DB --
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AccountSectionQuerry.class.getName()).log(Level.SEVERE, null, ex);
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

    private void loadTable(String sql, String param) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
            pst = con.prepareStatement(sql);
            if (param != null) pst.setString(1, param);
            rs = pst.executeQuery();
            jTable1.setModel(buildTableModel(rs));
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(AccountSectionQuerry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        setTitle("Account Section Query");
        setFont(new Font("Albertus", Font.BOLD, 12));
        getContentPane().setLayout(null);

        jLabel1 = new JLabel("Account-Section-Query");
        jLabel1.setFont(new Font("Segoe Script", Font.BOLD, 22));
        jLabel1.setForeground(Color.RED);
        jLabel1.setBounds(350, 30, 400, 60);

        // Radio buttons
        jRadioButton2 = makeRadio("Name-Wise",      180, 115, 130, 28);
        jRadioButton3 = makeRadio("Account No. Wise", 420, 115, 190, 28);
        jRadioButton1 = makeRadio("All Record",     660, 115, 130, 28);

        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1); bg.add(jRadioButton2); bg.add(jRadioButton3);

        // Mdi-Form button
        jButton1 = new JButton("Mdi-Form");
        jButton1.setFont(new Font("Segoe UI Black", Font.BOLD, 12));
        jButton1.setBounds(830, 112, 110, 32);

        // Table
        jTable1 = new JTable();
        jTable1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBounds(100, 155, 880, 420);

        jLabel2 = new JLabel(); // background placeholder
        jLabel2.setBounds(0, 0, 1200, 650);

        for (Component c : new Component[]{
            jLabel1, jRadioButton1, jRadioButton2, jRadioButton3,
            jButton1, jScrollPane1
        }) getContentPane().add(c);

        // --- Listeners ---
        jButton1.addActionListener(e -> { this.dispose(); });

        jRadioButton1.addActionListener(e -> {
            if (jRadioButton1.isSelected())
                loadTable("SELECT * FROM account_detail", null);
        });

        jRadioButton2.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Employee Name to Fetch:");
            if (st != null && !st.isEmpty())
                loadTable("SELECT * FROM account_detail WHERE employeename=?", st);
        });

        jRadioButton3.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Account No. to Fetch:");
            if (st != null && !st.isEmpty())
                loadTable("SELECT * FROM account_detail WHERE accountno=?", st);
        });
    }

    private JRadioButton makeRadio(String text, int x, int y, int w, int h) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI Black", Font.BOLD, 12));
        rb.setBounds(x, y, w, h);
        return rb;
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName()); break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AccountSectionQuerry.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(AccountSectionQuerry::new);
    }
}