import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/expanses?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tiger";

    private Connection        conn = null;
    private PreparedStatement pst  = null;
    private ResultSet         rs   = null;

    private JTextField     edtUser;
    private JPasswordField edtpswd;
    private JComboBox<String> cmboRole;
    private JButton        btnLogin, btnReset, btnSignUp;
    private JProgressBar   progressBar;

    public Login() {
        initComponents();
        setTitle("Expense Reimbursement System – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 460);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(230, 250, 255));
        setContentPane(root);

        // ── Header ──
        JLabel title = new JLabel("LOGIN WINDOW", SwingConstants.CENTER);
        title.setFont(new Font("Monotype Corsiva", Font.BOLD, 28));
        title.setForeground(new Color(51, 0, 51));
        title.setBorder(BorderFactory.createEmptyBorder(24, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);

        // ── Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 70));
        root.add(form, BorderLayout.CENTER);

        GridBagConstraints lc = lCon();
        GridBagConstraints fc = fCon();

        // Row 0 – Role
        lc.gridy = 0; fc.gridy = 0;
        form.add(makeLabel("User Role :"), lc);
        cmboRole = new JComboBox<>(new String[]{"Normal User", "Admin User"});
        cmboRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(cmboRole, fc);

        // Row 1 – Username
        lc.gridy = 1; fc.gridy = 1;
        form.add(makeLabel("Username :"), lc);
        edtUser = makeField();
        form.add(edtUser, fc);

        // Row 2 – Password
        lc.gridy = 2; fc.gridy = 2;
        form.add(makeLabel("Password :"), lc);
        edtpswd = new JPasswordField();
        styleField(edtpswd);
        form.add(edtpswd, fc);

        // Row 3 – Progress bar
        GridBagConstraints pc = new GridBagConstraints();
        pc.gridx = 0; pc.gridy = 3; pc.gridwidth = 2;
        pc.fill  = GridBagConstraints.HORIZONTAL;
        pc.insets = new Insets(14, 0, 4, 0);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(255, 0, 51));
        progressBar.setPreferredSize(new Dimension(0, 22));
        form.add(progressBar, pc);

        // ── Button row ──
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 14));
        btns.setOpaque(false);

        btnLogin  = makeButton("Login");
        btnReset  = makeButton("Reset");
        btnSignUp = makeButton("Sign Up");
        JLabel noAcc = new JLabel("Don't have an account?");
        noAcc.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btns.add(btnLogin);
        btns.add(btnReset);
        btns.add(Box.createHorizontalStrut(20));
        btns.add(noAcc);
        btns.add(btnSignUp);
        root.add(btns, BorderLayout.SOUTH);

        // ── Keyboard nav ──
        edtUser.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_TAB)
                    edtpswd.requestFocus();
            }
        });
        edtpswd.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP)
                    edtUser.requestFocus();
                else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doLogin();
            }
        });

        // ── Actions ──
        btnLogin.addActionListener(e -> doLogin());
        btnReset.addActionListener(e -> {
            edtUser.setText("");
            edtpswd.setText("");
            progressBar.setValue(0);
        });
        btnSignUp.addActionListener(e -> {
            new SignUpWindow().setVisible(true);
            dispose();
        });
    }

    private void doLogin() {
        progressBar.setValue(0);

        new Thread(() -> {
            try {
                progressBar.setValue(20);
                Thread.sleep(250);

                String username = edtUser.getText().trim();
                String password = new String(edtpswd.getPassword()).trim();
                String role     = cmboRole.getSelectedItem().toString();

                if (username.isEmpty()) {
                    showErr("Please enter your username.");
                    progressBar.setValue(0);
                    return;
                }
                if (password.isEmpty()) {
                    showErr("Please enter your password.");
                    progressBar.setValue(0);
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                progressBar.setValue(40);
                Thread.sleep(250);

                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                progressBar.setValue(60);
                Thread.sleep(250);

                pst = conn.prepareStatement(
                    "SELECT * FROM login WHERE username=? AND password=? AND Role=?");
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                rs = pst.executeQuery();
                progressBar.setValue(80);
                Thread.sleep(250);

                if (rs.next()) {
                    progressBar.setValue(100);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Login Successful!");
                        new MdiForm().setVisible(true);
                        dispose();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        showErr("Username, Password, or Role is incorrect!");
                        edtUser.setText("");
                        edtpswd.setText("");
                        progressBar.setValue(0);
                    });
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    showErr("Error: " + ex.getMessage());
                    progressBar.setValue(0);
                });
            } finally {
                try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
                try { if (pst  != null) pst.close();  } catch (SQLException ignored) {}
                try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
            }
        }).start();
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 20));
        l.setForeground(new Color(51, 0, 51));
        return l;
    }

    private JTextField makeField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JComponent f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(210, 34));
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Monotype Corsiva", Font.BOLD, 16));
        b.setBackground(new Color(0, 220, 220));
        b.setForeground(new Color(51, 0, 51));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private GridBagConstraints lCon() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 0, 10, 12);
        return c;
    }

    private GridBagConstraints fCon() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx   = 1;
        c.anchor  = GridBagConstraints.WEST;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets  = new Insets(10, 0, 10, 0);
        return c;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}