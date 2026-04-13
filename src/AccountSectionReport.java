import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.util.Date;
import java.util.logging.*;
import javax.swing.*;

/**
 * Account Section Report - Pure Java Swing (no NetBeans dependencies)
 * @author RAJANISH
 */
public class AccountSectionReport extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    private JLabel    jLabel1, jLabel2;
    private JTextArea jTextArea1;
    private JScrollPane jScrollPane1;
    private JButton   jButton1, jButton2, jButton3, jButton4;

    public AccountSectionReport() {
        initComponents();
        // this.setJMenuBar(AppMenuBar.createMenu(this));
        this.setLocation(200, 200);
        this.setSize(1200, 820);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        generateReport();
        this.setVisible(true);
    }

    // --------------------------------------------------------- UI SETUP -----
    private void initComponents() {
        setTitle("Account Section Report");
        setFont(new Font("Antique Olive", Font.BOLD, 12));
        getContentPane().setLayout(null);

        jLabel1 = new JLabel("Account-Section-Report");
        jLabel1.setFont(new Font("Segoe Print", Font.BOLD, 24));
        jLabel1.setForeground(new Color(204, 0, 51));
        jLabel1.setBounds(350, 20, 430, 50);

        jTextArea1 = new JTextArea();
        jTextArea1.setFont(new Font("Segoe UI Black", Font.BOLD, 13));
        jTextArea1.setEditable(false);
        jScrollPane1 = new JScrollPane(jTextArea1);
        jScrollPane1.setBounds(80, 90, 1090, 600);

        jButton1 = makeButton("Generate", 380, 705, 120, 32);
        jButton2 = makeButton("Print",    508, 705, 110, 32);
        jButton3 = makeButton("Close",    625, 705, 120, 32);
        jButton4 = makeButton("Mdi-Form", 752, 705, 120, 32);

        jLabel2 = new JLabel(); // background placeholder
        jLabel2.setBounds(0, 0, 1200, 820);

        for (Component c : new Component[]{
            jLabel1, jScrollPane1, jButton1, jButton2, jButton3, jButton4
        }) getContentPane().add(c);

        // --- Listeners ---
        jButton1.addActionListener(e -> generateReport());
        jButton2.addActionListener(e -> printReport());
        jButton3.addActionListener(e -> dispose());
        jButton4.addActionListener(e -> { this.dispose(); }); // MdiForm.show() when available
    }

    private JButton makeButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBounds(x, y, w, h);
        return b;
    }

    // --------------------------------------------------- Report Logic -------
    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("*".repeat(72)).append("\n");
        sb.append("\t\t\t\t Account-Section Report\n");
        sb.append("*".repeat(72)).append("\n");
        sb.append(new Date()).append("\n\n");
        sb.append("=".repeat(72)).append("\n");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
            pst = con.prepareStatement("SELECT * FROM account_detail");
            rs  = pst.executeQuery();

            sb.append(String.format("%-18s %-15s %-15s %-20s %-14s %-18s %-14s%n",
                    "Bank Name", "Account No.", "Customer Id",
                    "Customer Name", "Total Amount", "Nature Account", "Acc.Open Date"));
            sb.append("-".repeat(115)).append("\n");

            while (rs.next()) {
                sb.append(String.format("%-18s %-15s %-15s %-20s %-14s %-18s %-14s%n",
                        nullSafe(rs.getString(2)), nullSafe(rs.getString(3)),
                        nullSafe(rs.getString(4)), nullSafe(rs.getString(5)),
                        nullSafe(rs.getString(6)), nullSafe(rs.getString(7)),
                        nullSafe(rs.getString(8))));
            }
            sb.append("\n\n\n\t\t\t\t\t\t\t\tSignature");
            jTextArea1.setText(sb.toString());

        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            Logger.getLogger(AccountSectionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void printReport() {
        try {
            jTextArea1.print();
        } catch (PrinterException ex) {
            Logger.getLogger(AccountSectionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountSectionReport::new);
    }
}