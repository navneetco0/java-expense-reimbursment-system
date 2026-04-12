import java.awt.*;
import javax.swing.*;

// ══════════════════════════════════════════════════════════════════
//  AppMenuBar  –  shared menu bar factory
// ══════════════════════════════════════════════════════════════════
class AppMenuBar {

    public static JMenuBar createMenu(final JFrame host) {
        JMenuBar bar = new JMenuBar();
        bar.setFont(new Font("Segoe UI Black", Font.BOLD, 12));

        // ── User Module ──────────────────────────────────────────
        JMenu userMenu = new JMenu("User Module");
        styleMenu(userMenu);

        JMenuItem addUser    = item("Add User");
        JMenuItem deleteUser = item("Delete User");
        JMenuItem exit       = item("Exit");

        addUser.addActionListener(e -> {
            new SignUpWindow().setVisible(true);
        });
        deleteUser.addActionListener(e ->
            JOptionPane.showMessageDialog(host, "Delete User – not yet implemented."));
        exit.addActionListener(e -> System.exit(0));

        userMenu.add(addUser);
        userMenu.add(deleteUser);
        userMenu.addSeparator();
        userMenu.add(exit);
        bar.add(userMenu);

        // ── Modules ──────────────────────────────────────────────
        JMenu modMenu = new JMenu("Modules");
        styleMenu(modMenu);

        String[][] modules = {
            {"User-Detail",            "EmployeeDetail"},
            {"Employee-Detail",        "AccountDetail"},
            {"Managers-Detail",        "BookingDetail"}, 
            {"Expense-Categories",     "DonerDetail"},   
            {"Expenses-Detail",        "CustomerDetail"}, 
            {"Claims-Detail",          "EnquiryDetail"}, 
            {"Approval-Detail",        "InventoryDetail"}, 
            {"Reimbursements-Detail",  "SalaryDetail"}, 
            {"Audit-Log-Detail",       "TaskDetail"}, 
        };
        for (String[] m : modules) {
            JMenuItem mi = item(m[0]);
            String cls   = m[1];
            mi.addActionListener(e -> openClass(cls, host));
            modMenu.add(mi);
        }
        bar.add(modMenu);

        // ── Queries ──────────────────────────────────────────────
        JMenu queryMenu = new JMenu("Queries");
        styleMenu(queryMenu);

        String[][] queries = {
            {"Employee-Section-Query",          "EmployeeSectionQuery"}, 
            {"Managers-Section-Query",          "BookingSectionQuerry"}, 
            {"Expense-Query",                   "DonerSectionQuery"}, 
            {"Expense-Categories-Query",        "AccountSectionQuerry"}, 
            {"Claims-Section-Query",            "EnquirySectionQuery"},
            {"Approval-Section-Query",          "SalarySectionQuery"}, 
        };
        for (String[] q : queries) {
            JMenuItem mi = item(q[0]);
            String cls   = q[1];
            mi.addActionListener(e -> openClass(cls, host));
            queryMenu.add(mi);
        }
        bar.add(queryMenu);

        // ── Reports ──────────────────────────────────────────────
        JMenu repMenu = new JMenu("Reports");
        styleMenu(repMenu);

        String[][] reports = {
            {"Employee-Section-Report",         "EmployeeSectionReport"},
            {"Managers-Section-Report",         "BookingSectionReport"}, 
            {"Expense-Section-Report",          "ExpenseSectionReport"},
            {"Claims-Section-Report",           "EnquirySectionReport"}, 
            {"Approval-Report",                 "DonerSectionReport"}, 
            {"Account-Detail-Report",           "AccountSectionReport"},
        };
        for (String[] r : reports) {
            JMenuItem mi = item(r[0]);
            String cls   = r[1];
            mi.addActionListener(e -> openClass(cls, host));
            repMenu.add(mi);
        }
        bar.add(repMenu);

        return bar;
    }

    /** Reflectively open a form class by simple name. */
    private static void openClass(String simpleName, JFrame host) {
        try {
            Class<?> cls = Class.forName(simpleName);
            JFrame form = (JFrame) cls.getDeclaredConstructor().newInstance();
            form.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(host,
                simpleName + " not yet available.\n(" + ex.getMessage() + ")");
        }
    }

    private static JMenuItem item(String text) {
        JMenuItem mi = new JMenuItem(text);
        mi.setFont(new Font("Segoe UI Black", Font.PLAIN, 12));
        return mi;
    }

    private static void styleMenu(JMenu m) {
        m.setFont(new Font("Segoe UI Black", Font.BOLD, 12));
    }
}

// ══════════════════════════════════════════════════════════════════
//  MdiForm  –  main application window (pure Swing, no NB layout)
// ══════════════════════════════════════════════════════════════════
public class MdiForm extends JFrame {

    public MdiForm() {
        initComponents();
        setJMenuBar(AppMenuBar.createMenu(this));
        setTitle("Expense Reimbursement System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Root panel with gradient-like background
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(200, 240, 255),
                    0, getHeight(), new Color(160, 210, 240)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(true);
        setContentPane(root);

        // ── Welcome label ──
        JLabel welcome = new JLabel(
            "<html><center>Welcome to<br>Expense Reimbursement System</center></html>",
            SwingConstants.CENTER);
        welcome.setFont(new Font("Monotype Corsiva", Font.BOLD, 36));
        welcome.setForeground(new Color(51, 0, 102));
        welcome.setBorder(BorderFactory.createEmptyBorder(80, 20, 20, 20));
        root.add(welcome, BorderLayout.CENTER);

        // ── Subtitle ──
        JLabel sub = new JLabel(
            "Use the menu bar above to navigate modules.",
            SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        sub.setForeground(new Color(80, 80, 80));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        root.add(sub, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}