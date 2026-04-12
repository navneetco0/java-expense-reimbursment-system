import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class SignUpWindow extends JFrame {

    // ── DB constants ──────────────────────────────────────────────
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/expanses?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tiger";

    // ── DB handles ───────────────────────────────────────────────
    private Connection        conn = null;
    private PreparedStatement pst  = null;
    private ResultSet         rs   = null;

    // ── UI components ────────────────────────────────────────────
    private JTextField    txtName, txtUserName, txtEmail;
    private JPasswordField txtPswd;
    private JComboBox<String> cmbRole;
    private JButton       btnSignUp, btnClose, btnLogIn;
    private JLabel        lblTitle;

    // ─────────────────────────────────────────────────────────────
    public SignUpWindow() {
        initComponents();
        setTitle("Expense Reimbursement System – New User");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    // ── Build UI ─────────────────────────────────────────────────
    private void initComponents() {
        // Content pane with BorderLayout
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(230, 250, 255));
        setContentPane(root);

        // ── Header ──
        lblTitle = new JLabel("NEW USER REGISTRATION", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Monotype Corsiva", Font.BOLD, 26));
        lblTitle.setForeground(new Color(51, 0, 51));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        root.add(lblTitle, BorderLayout.NORTH);

        // ── Form panel ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        root.add(form, BorderLayout.CENTER);

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        // Row 0 – Name
        lc.gridy = 0; fc.gridy = 0;
        form.add(makeLabel("Name :"), lc);
        txtName = makeField(); form.add(txtName, fc);

        // Row 1 – User ID
        lc.gridy = 1; fc.gridy = 1;
        form.add(makeLabel("User ID :"), lc);
        txtUserName = makeField(); form.add(txtUserName, fc);

        // Row 2 – Password
        lc.gridy = 2; fc.gridy = 2;
        form.add(makeLabel("Password :"), lc);
        txtPswd = new JPasswordField();
        styleField(txtPswd); form.add(txtPswd, fc);

        // Row 3 – Email
        lc.gridy = 3; fc.gridy = 3;
        form.add(makeLabel("Email :"), lc);
        txtEmail = makeField(); form.add(txtEmail, fc);

        // Row 4 – Role
        lc.gridy = 4; fc.gridy = 4;
        form.add(makeLabel("User Role :"), lc);
        cmbRole = new JComboBox<>(new String[]{"Normal User", "Admin User"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(cmbRole, fc);

        // ── Button panel ──
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        btns.setOpaque(false);

        btnSignUp = makeButton("Sign Up");
        btnClose  = makeButton("Close");
        btnLogIn  = makeButton("Already have account? Log In");

        btns.add(btnSignUp);
        btns.add(btnClose);
        btns.add(btnLogIn);
        root.add(btns, BorderLayout.SOUTH);

        // ── Keyboard tab order ──
        KeyListener tabDown = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_TAB) {
                    ((Component) e.getSource()).transferFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    ((Component) e.getSource()).transferFocusBackward();
                }
            }
        };
        txtName.addKeyListener(tabDown);
        txtUserName.addKeyListener(tabDown);
        txtPswd.addKeyListener(tabDown);
        txtEmail.addKeyListener(tabDown);

        // ── Actions ──
        btnSignUp.addActionListener(e -> signUpActionPerformed());
        btnClose.addActionListener(e -> dispose());
        btnLogIn.addActionListener(e -> {
            new Login().setVisible(true);
            dispose();
        });
    }

    // ── Sign-up logic ────────────────────────────────────────────
    private void signUpActionPerformed() {
        String name     = txtName.getText().trim();
        String userName = txtUserName.getText().trim();
        String password = new String(txtPswd.getPassword()).trim();
        String email    = txtEmail.getText().trim();
        String role     = cmbRole.getSelectedItem().toString();

        if (name.isEmpty() || userName.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showError("Please fill out all fields.");
            return;
        }
        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }
        if (!isValidPassword(password)) {
            showError("Password must be at least 8 characters and include letters and numbers.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            pst = conn.prepareStatement("SELECT * FROM login WHERE username = ?");
            pst.setString(1, userName);
            rs = pst.executeQuery();
            if (rs.next()) {
                showError("Username already exists. Please choose a different one.");
                return;
            }

            pst = conn.prepareStatement(
                "INSERT INTO login(name, username, password, Email, Role) VALUES(?,?,?,?,?)");
            pst.setString(1, name);
            pst.setString(2, userName);
            pst.setString(3, password);
            pst.setString(4, email);
            pst.setString(5, role);

            if (pst.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "New User Signed Up Successfully!");
                clearFields();
                new Login().setVisible(true);
                dispose();
            } else {
                showError("Sign-Up failed. Please try again.");
            }
        } catch (Exception ex) {
            showError("An error occurred: " + ex.getMessage());
        } finally {
            closeResources();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────
    private void clearFields() {
        txtName.setText("");
        txtUserName.setText("");
        txtPswd.setText("");
        txtEmail.setText("");
        cmbRole.setSelectedIndex(0);
    }

    private void closeResources() {
        try { if (rs   != null) rs.close();   } catch (SQLException ignored) {}
        try { if (pst  != null) pst.close();  } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean isValidPassword(String pwd) {
        return pwd.length() >= 8
            && pwd.matches(".*[A-Za-z].*")
            && pwd.matches(".*[0-9].*");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── UI factory helpers ────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT);
        l.setFont(new Font("Monotype Corsiva", Font.BOLD, 18));
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
        f.setPreferredSize(new Dimension(220, 32));
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

    private GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(8, 0, 8, 12);
        return c;
    }

    private GridBagConstraints fieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill   = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(8, 0, 8, 0);
        return c;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUpWindow().setVisible(true));
    }
}