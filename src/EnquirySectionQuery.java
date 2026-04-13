import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Enquiry Section Query - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class EnquirySectionQuery extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    private JLabel       jLabel1, jLabel2;
    private JRadioButton jRadioButton1, jRadioButton2, jRadioButton3;
    private JTable       jTable1;
    private JScrollPane  jScrollPane1;
    private JButton      jButton1;

    public EnquirySectionQuery() {
        initComponents();
        // this.setJMenuBar(AppMenuBar.createMenu(this));
        this.setLocation(200, 200);
        this.setSize(1200, 650);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
            Logger.getLogger(EnquirySectionQuery.class.getName()).log(Level.SEVERE, null, ex);
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
            DefaultTableCellRenderer hr = (DefaultTableCellRenderer)
                    jTable1.getTableHeader().getDefaultRenderer();
            hr.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(EnquirySectionQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        setTitle("Enquiry Section Query");
        getContentPane().setLayout(null);

        jLabel1 = new JLabel("Enquiry-Section-Query");
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        jLabel1.setForeground(new Color(51, 51, 51));
        jLabel1.setBounds(380, 35, 310, 55);

        // Radio buttons
        jRadioButton1 = makeRadio("All-Record",  240, 115, 130, 33);
        jRadioButton3 = makeRadio("Name-Wise",   440, 115, 145, 33);
        jRadioButton2 = makeRadio("Id-Wise",     650, 115, 130, 33);

        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1); bg.add(jRadioButton2); bg.add(jRadioButton3);

        jButton1 = new JButton("Mdi-Form");
        jButton1.setFont(new Font("Arial Black", Font.BOLD, 12));
        jButton1.setForeground(new Color(51, 0, 51));
        jButton1.setBounds(840, 112, 110, 33);

        jTable1 = new JTable();
        jTable1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jScrollPane1 = new JScrollPane(jTable1);
        jScrollPane1.setBounds(150, 165, 820, 410);

        jLabel2 = new JLabel(); // background placeholder
        jLabel2.setForeground(new Color(204, 0, 0));
        jLabel2.setBounds(0, 0, 1200, 650);

        for (Component c : new Component[]{
            jLabel1, jRadioButton1, jRadioButton2, jRadioButton3,
            jButton1, jScrollPane1
        }) getContentPane().add(c);

        // --- Listeners ---
        jButton1.addActionListener(e -> { this.dispose(); });

        jRadioButton1.addActionListener(e -> {
            if (jRadioButton1.isSelected())
                loadTable("SELECT * FROM enquiry", null);
        });

        jRadioButton3.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Name to Fetch:");
            if (st != null && !st.isEmpty())
                loadTable("SELECT * FROM enquiry WHERE employeename=?", st);
        });

        jRadioButton2.addActionListener(e -> {
            String st = JOptionPane.showInputDialog(this, "Enter Enquiry Id to Fetch:");
            if (st != null && !st.isEmpty()) {
                loadTable("SELECT * FROM enquiry WHERE enquiryid=?", st);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry Not found!! Please try another Record");
            }
        });
    }

    private JRadioButton makeRadio(String text, int x, int y, int w, int h) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rb.setForeground(new Color(51, 51, 51));
        rb.setBounds(x, y, w, h);
        return rb;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EnquirySectionQuery::new);
    }
}