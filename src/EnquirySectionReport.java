import java.awt.*;
import java.sql.*;
import java.util.logging.*;
import javax.swing.*;

/**
 * Claims Section Report
 */
public class EnquirySectionReport extends JFrame {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pst;

    private JLabel      jLabel1, jLabel2;
    private JTextArea   jTextArea1;
    private JScrollPane jScrollPane1;
    private JButton     jButton1, jButton2, jButton3;

    public EnquirySectionReport() {
        initComponents();
        this.setLocation(200, 200);
        this.setSize(1200, 820);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Connect();
        generateReport();
        this.setVisible(true);
    }

    private void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/expanses?useSSL=false", "root", "tiger");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(EnquirySectionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() {
        setTitle("Claims Section Report");
        getContentPane().setLayout(new BorderLayout());

        jLabel1 = new JLabel("Claims Report");
        jLabel1.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        jLabel1.setForeground(new Color(0, 51, 51));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        jLabel2 = new JLabel("<html>This report displays all expense claims in the system with their status.</html>");
        jLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jLabel2.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        jTextArea1 = new JTextArea();
        jTextArea1.setFont(new Font("Monospaced", Font.PLAIN, 11));
        jTextArea1.setEditable(false);
        jTextArea1.setText("Claims Report:\n\nNo data loaded yet.\nClick 'Generate Report' to load claim data.");
        jScrollPane1 = new JScrollPane(jTextArea1);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(jLabel1, BorderLayout.NORTH);
        topPanel.add(jLabel2, BorderLayout.CENTER);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        jButton1 = new JButton("Generate Report");
        jButton1.setFont(new Font("Segoe UI Black", Font.BOLD, 13));
        jButton1.setForeground(new Color(0, 51, 51));

        jButton2 = new JButton("Print");
        jButton2.setFont(new Font("Segoe UI Black", Font.BOLD, 13));
        jButton2.setForeground(new Color(0, 51, 51));

        jButton3 = new JButton("Close");
        jButton3.setFont(new Font("Segoe UI Black", Font.BOLD, 13));
        jButton3.setForeground(new Color(0, 51, 51));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.add(jButton1);
        btnPanel.add(jButton2);
        btnPanel.add(jButton3);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        jButton1.addActionListener(e -> generateReport());
        jButton2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Print functionality not yet implemented."));
        jButton3.addActionListener(e -> this.dispose());
    }

    private void generateReport() {
        try {
            Connect();
            StringBuilder report = new StringBuilder();
            report.append("========== CLAIMS SECTION REPORT ==========\n\n");
            
            String sql = "SELECT * FROM claim";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            report.append(String.format("%-10s %-15s %-15s %-20s%n", "Claim ID", "Employee ID", "Amount", "Status"));
            report.append("=".repeat(60)).append("\n");

            int count = 0;
            while (rs.next()) {
                report.append(String.format("%-10s %-15s %-15.2f %-20s%n",
                    rs.getString("claimId"),
                    rs.getString("employeeId"),
                    rs.getDouble("amount"),
                    rs.getString("status")));
                count++;
            }
            report.append("\n").append("=".repeat(60)).append("\n");
            report.append("Total Claims: ").append(count).append("\n");

            jTextArea1.setText(report.toString());
            rs.close();
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
            Logger.getLogger(EnquirySectionReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EnquirySectionReport().setVisible(true));
    }
}
