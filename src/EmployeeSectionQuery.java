import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Employee Section Query - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class EmployeeSectionQuery extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    private JLabel       jLabel1, jLabel3;
    private JRadioButton jRadioButton1, jRadioButton2, jRadioButton3;
    private JTable       jTable1;
    private JScrollPane  jScrollPane1;
    private JButton      jButton1;

    public EmployeeSectionQuery() {
        initComponents();
        // this.setJMenuBar(AppMenuBar.createMenu(this));
        this.setLocation(200, 200);
        this.setSize(1200, 650);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Connect();
        this.setVisible(true);
    }

    // ------------------------------------------------------------------ DB --
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(EmployeeSectionQuery.class.getName()).log(Level.SEVERE, null, ex);
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
            if (param == null) {
                pst = con.prepareStatement(sql);
            } else {
                pst = con.prepareStatement(sql);
                pst.setString(1, param);
            }
            rs = pst.executeQuery();
            jTable1.setModel(buildTableModel(rs));
            DefaultTableCellRenderer hr = (DefaultTableCellRenderer)
                    jTable1.getTableHeader().getDefaultRenderer();
            hr.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(EmployeeSectionQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        setTitle("Employee Section Query");
        getContentPane().setLayout(null);

        jLabel3 = new JLabel("Employee-Section-Query");
        jLabel3.setFont(new Font("Comic Sans MS", Font.BOLD, 19));
        jLabel3.setForeground(new Color(0, 51, 51));
        jLabel3.setBounds(380, 40, 320, 50);

        // Radio buttons
        jRadioButton1 = makeRadio("Name Wise",  220, 120, 150, 30);
        jRadioButton2 = makeRadio("Id-Wise",    470, 120, 120, 30);
        jRadioButton3 = makeRadio("All Record", 690, 120, 160, 30);

        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1); bg.add(jRadioButton2); bg.add(jRadioButton3);

        // Table
        jTable1 = new JTable();
        jTable1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBounds(70, 170, 960, 400);

        // Button
        jButton1 = new JButton("Home-Page");
        jButton1.setFont(new Font("Segoe UI Black", Font.BOLD, 14));
        jButton1.setForeground(new Color(0, 51, 51));
        jButton1.setBounds(1030, 30, 130, 32);

        // Background label
        jLabel1 = new JLabel();
        jLabel1.setBounds(0, 0, 1200, 650);

        for (Component c : new Component[]{
            jLabel3, jRadioButton1, jRadioButton2, jRadioButton3,
            jScrollPane1, jButton1
        }) getContentPane().add(c);

        // --- Listeners ---
        jButton1.addActionListener(e -> { this.dispose(); });

        jRadioButton3.addActionListener(e -> {
            if (jRadioButton3.isSelected())
                loadTable("SELECT * FROM employee", null);
        });

        jRadioButton1.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Employee Name to Fetch:");
            if (st != null && !st.isEmpty())
                loadTable("SELECT * FROM employee WHERE employeename=?", st);
        });

        jRadioButton2.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Employee Id to Fetch:");
            if (st != null && !st.isEmpty())
                loadTable("SELECT * FROM employee WHERE employeeid=?", st);
            else
                JOptionPane.showMessageDialog(null, "Sorry Not found!! Please try another Record");
        });
    }

    private JRadioButton makeRadio(String text, int x, int y, int w, int h) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        rb.setForeground(new Color(0, 51, 51));
        rb.setBounds(x, y, w, h);
        return rb;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeSectionQuery::new);
    }
}