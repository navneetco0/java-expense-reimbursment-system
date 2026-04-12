import java.sql.*;
import javax.swing.JOptionPane;

/**
 * DatabaseInitializer - Automatically creates and populates MySQL database
 * This class checks if the database exists, creates it if needed, and populates it with sample data
 */
public class DatabaseInitializer {

    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    private static final String DB_NAME = "expanses";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tiger";
    private static final String JDBC_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT;

    /**
     * Initialize database - creates it if it doesn't exist and populates with data
     */
    public static boolean initializeDatabase() {
        try {
            // Step 1: Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ MySQL driver loaded");

            // Step 2: Check if database exists
            if (!databaseExists()) {
                System.out.println("→ Database 'expanses' not found, creating...");
                createDatabase();
            } else {
                System.out.println("✓ Database 'expanses' already exists");
            }

            // Step 3: Check and create tables
            checkAndCreateTables();

            // Step 4: Check and populate sample data
            populateSampleDataIfEmpty();

            System.out.println("✓ Database initialization complete");
            return true;

        } catch (ClassNotFoundException e) {
            showErrorDialog("MySQL Driver Error", 
                "MySQL JDBC driver not found. Please ensure mysql-connector-j is in the classpath.\n" + e.getMessage());
            return false;
        } catch (Exception e) {
            showErrorDialog("Database Initialization Error", 
                "Failed to initialize database:\n" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if the database exists
     */
    private static boolean databaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + DB_NAME + "'");
            return rs.next();
        }
    }

    /**
     * Create the database
     */
    private static void createDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME + 
                " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("✓ Database 'expanses' created successfully");
        }
    }

    /**
     * Check if tables exist and create them if they don't
     */
    private static void checkAndCreateTables() throws SQLException {
        String dbUrl = JDBC_URL + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            // Count tables in database
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '" + DB_NAME + "'");
            rs.next();
            int tableCount = rs.getInt(1);

            if (tableCount == 0) {
                System.out.println("→ No tables found, creating all tables...");
                createAllTables(conn);
                System.out.println("✓ All tables created successfully");
            } else {
                System.out.println("✓ Tables already exist (" + tableCount + " tables found)");
            }
        }
    }

    /**
     * Create all required tables
     */
    private static void createAllTables(Connection conn) throws SQLException {
        String[] createTableStatements = {
            // Login table
            "CREATE TABLE IF NOT EXISTS login (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  name VARCHAR(100) NOT NULL," +
            "  username VARCHAR(50) NOT NULL UNIQUE," +
            "  password VARCHAR(100) NOT NULL," +
            "  Email VARCHAR(100)," +
            "  Role VARCHAR(50)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Employee table
            "CREATE TABLE IF NOT EXISTS employee (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  employeeId VARCHAR(20) NOT NULL UNIQUE," +
            "  employeename VARCHAR(100) NOT NULL," +
            "  dob VARCHAR(20)," +
            "  companyId VARCHAR(20)," +
            "  dateofjoin VARCHAR(20)," +
            "  Salary DECIMAL(12,2)," +
            "  postName VARCHAR(50)," +
            "  address VARCHAR(200)," +
            "  pincode VARCHAR(10)," +
            "  contactno VARCHAR(20)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Manager table
            "CREATE TABLE IF NOT EXISTS manager (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  managerId VARCHAR(20) NOT NULL UNIQUE," +
            "  managerName VARCHAR(100) NOT NULL," +
            "  department VARCHAR(80)," +
            "  email VARCHAR(100)," +
            "  phone VARCHAR(20)," +
            "  hireDate VARCHAR(20)," +
            "  salary DECIMAL(12,2)," +
            "  status VARCHAR(20) DEFAULT 'Active'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Expense category table
            "CREATE TABLE IF NOT EXISTS expensecategory (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  categoryId VARCHAR(20) NOT NULL UNIQUE," +
            "  categoryName VARCHAR(100) NOT NULL," +
            "  description VARCHAR(255)," +
            "  budgetLimit DECIMAL(12,2) DEFAULT 0," +
            "  isActive VARCHAR(10) DEFAULT 'Yes'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Claim table
            "CREATE TABLE IF NOT EXISTS claim (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  claimId VARCHAR(20) NOT NULL UNIQUE," +
            "  employeeId VARCHAR(20)," +
            "  categoryId VARCHAR(20)," +
            "  claimDate VARCHAR(20)," +
            "  amount DECIMAL(12,2) DEFAULT 0," +
            "  description VARCHAR(255)," +
            "  status VARCHAR(20) DEFAULT 'Pending'," +
            "  approvedBy VARCHAR(50)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Approval table
            "CREATE TABLE IF NOT EXISTS approval (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  approvalId VARCHAR(20) NOT NULL UNIQUE," +
            "  claimId VARCHAR(20)," +
            "  approverId VARCHAR(20)," +
            "  approvalDate VARCHAR(20)," +
            "  amount DECIMAL(12,2) DEFAULT 0," +
            "  status VARCHAR(20) DEFAULT 'Pending'," +
            "  remarks VARCHAR(255)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Reimbursement table
            "CREATE TABLE IF NOT EXISTS reimbursement (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  reimbId VARCHAR(20) NOT NULL UNIQUE," +
            "  claimId VARCHAR(20)," +
            "  employeeId VARCHAR(20)," +
            "  processedDate VARCHAR(20)," +
            "  amount DECIMAL(12,2) DEFAULT 0," +
            "  paymentMode VARCHAR(30) DEFAULT 'Bank Transfer'," +
            "  transactionRef VARCHAR(50)," +
            "  status VARCHAR(20) DEFAULT 'Processed'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Account detail table
            "CREATE TABLE IF NOT EXISTS account_detail (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  bankname VARCHAR(100)," +
            "  accountno VARCHAR(20) NOT NULL UNIQUE," +
            "  employeeid VARCHAR(20)," +
            "  employeename VARCHAR(100)," +
            "  grosssal DECIMAL(12,2)," +
            "  natureofacount VARCHAR(50)," +
            "  dateofopen VARCHAR(20)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Audit log table
            "CREATE TABLE IF NOT EXISTS auditlog (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  logId VARCHAR(30) NOT NULL UNIQUE," +
            "  userId VARCHAR(30)," +
            "  action VARCHAR(20)," +
            "  targetTable VARCHAR(50)," +
            "  targetId VARCHAR(30)," +
            "  timestamp VARCHAR(30)," +
            "  oldValue VARCHAR(255)," +
            "  newValue VARCHAR(255)," +
            "  ipAddress VARCHAR(30)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Customer detail table
            "CREATE TABLE IF NOT EXISTS customerdetail (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  cusid VARCHAR(20) NOT NULL UNIQUE," +
            "  custname VARCHAR(100) NOT NULL," +
            "  address VARCHAR(255)," +
            "  city VARCHAR(100)," +
            "  pincode VARCHAR(10)," +
            "  noofperson INT," +
            "  identityproof VARCHAR(50)," +
            "  contno VARCHAR(20)," +
            "  noofroom INT," +
            "  dateofbooking VARCHAR(20)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // Recurring expense table
            "CREATE TABLE IF NOT EXISTS RecurringExpanse (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  RecurringExpanseID VARCHAR(20) NOT NULL UNIQUE," +
            "  FirstName VARCHAR(100)," +
            "  LastName VARCHAR(100)," +
            "  EmployeeID VARCHAR(20)," +
            "  Year INT," +
            "  Month VARCHAR(20)," +
            "  ExpenseAmount DECIMAL(12,2)," +
            "  Category VARCHAR(50)," +
            "  Description VARCHAR(255)," +
            "  Status VARCHAR(20)," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : createTableStatements) {
                stmt.executeUpdate(sql);
            }
        }
    }

    /**
     * Check if tables have data, and populate with sample data if empty
     */
    private static void populateSampleDataIfEmpty() throws SQLException {
        String dbUrl = JDBC_URL + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";
        
        try (Connection conn = DriverManager.getConnection(dbUrl, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            // Check if login table has data (excludes admin user)
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM login WHERE role != 'Admin User'");
            rs.next();
            
            if (rs.getInt(1) == 0) {
                System.out.println("→ Database is empty, populating with sample data...");
                populateSampleData(conn);
                System.out.println("✓ Sample data populated successfully");
            } else {
                System.out.println("✓ Database already populated (" + rs.getInt(1) + " non-admin users found)");
            }
        }
    }

    /**
     * Populate all tables with realistic sample data
     */
    private static void populateSampleData(Connection conn) throws SQLException {
        // Insert sample data into all tables
        String[] insertStatements = {
            // Login data
            "INSERT IGNORE INTO login(name, username, password, Email, Role) VALUES " +
            "('Admin User', 'admin', 'admin123', 'admin@company.com', 'Admin User')," +
            "('John Smith', 'john.smith', 'john123456', 'john.smith@company.com', 'Normal User')," +
            "('Sarah Johnson', 'sarah.johnson', 'sarah123456', 'sarah.johnson@company.com', 'Normal User')," +
            "('Michael Brown', 'michael.brown', 'michael123456', 'michael.brown@company.com', 'Normal User')," +
            "('Emily Davis', 'emily.davis', 'emily123456', 'emily.davis@company.com', 'Normal User')," +
            "('Robert Wilson', 'robert.wilson', 'robert123456', 'robert.wilson@company.com', 'Normal User')",

            // Employee data
            "INSERT IGNORE INTO employee(employeeId, employeename, dob, companyId, dateofjoin, Salary, postName, address, pincode, contactno) VALUES " +
            "('EMP001', 'John Smith', '1990-05-15', 'COMP001', '2020-01-10', 65000.00, 'Senior Developer', '123 Oak Street', '10001', '555-0101')," +
            "('EMP002', 'Sarah Johnson', '1992-08-22', 'COMP001', '2021-03-15', 72000.00, 'Project Manager', '456 Maple Ave', '90001', '555-0102')," +
            "('EMP003', 'Michael Brown', '1988-11-30', 'COMP001', '2019-06-20', 68000.00, 'Senior Analyst', '789 Pine Road', '60601', '555-0103')," +
            "('EMP004', 'Emily Davis', '1995-02-14', 'COMP002', '2022-07-01', 58000.00, 'Junior Developer', '321 Elm Street', '77001', '555-0104')," +
            "('EMP005', 'Robert Wilson', '1991-09-08', 'COMP002', '2020-11-05', 64000.00, 'QA Engineer', '654 Cedar Lane', '85001', '555-0105')," +
            "('EMP006', 'Jennifer Garcia', '1993-03-25', 'COMP001', '2021-02-10', 70000.00, 'Business Analyst', '987 Birch Way', '19101', '555-0106')," +
            "('EMP007', 'David Martinez', '1989-07-19', 'COMP002', '2020-09-15', 66000.00, 'Systems Admin', '147 Walnut Court', '78201', '555-0107')," +
            "('EMP008', 'Lisa Anderson', '1994-12-03', 'COMP001', '2021-10-01', 60000.00, 'HR Specialist', '258 Spruce Drive', '92101', '555-0108')",

            // Manager data
            "INSERT IGNORE INTO manager(managerId, managerName, department, email, phone, hireDate, salary, status) VALUES " +
            "('MGR001', 'Thomas Anderson', 'Engineering', 'thomas@company.com', '555-0201', '2018-05-10', 95000.00, 'Active')," +
            "('MGR002', 'Patricia Taylor', 'Finance', 'patricia@company.com', '555-0202', '2017-08-15', 92000.00, 'Active')," +
            "('MGR003', 'James Martinez', 'Operations', 'james@company.com', '555-0203', '2019-01-20', 88000.00, 'Active')," +
            "('MGR004', 'Maria Garcia', 'Human Resources', 'maria@company.com', '555-0204', '2018-11-10', 85000.00, 'Active')," +
            "('MGR005', 'Christopher Lee', 'Sales', 'christopher@company.com', '555-0205', '2020-03-05', 90000.00, 'Active')",

            // Expense category data
            "INSERT IGNORE INTO expensecategory(categoryId, categoryName, description, budgetLimit, isActive) VALUES " +
            "('CAT001', 'Travel - Accommodation', 'Hotel, lodging expenses', 50000.00, 'Yes')," +
            "('CAT002', 'Travel - Transportation', 'Flight, train, taxi, parking', 40000.00, 'Yes')," +
            "('CAT003', 'Meals & Entertainment', 'Client meals, team lunch', 25000.00, 'Yes')," +
            "('CAT004', 'Office Supplies', 'Stationery, equipment', 15000.00, 'Yes')," +
            "('CAT005', 'Training & Development', 'Courses, certifications', 35000.00, 'Yes')," +
            "('CAT006', 'Conference & Events', 'Registration fees, booth', 60000.00, 'Yes')," +
            "('CAT007', 'Mobile & Internet', 'Mobile bills, internet', 12000.00, 'Yes')," +
            "('CAT008', 'Equipment & Hardware', 'Laptops, monitors', 80000.00, 'Yes')," +
            "('CAT009', 'Utilities & Rent', 'Office utilities, rent', 100000.00, 'Yes')",

            // Claim data
            "INSERT IGNORE INTO claim(claimId, employeeId, categoryId, claimDate, amount, description, status, approvedBy) VALUES " +
            "('CLM001', 'EMP001', 'CAT001', '2024-01-05', 2500.00, 'Hotel stay for client meeting', 'Approved', 'MGR001')," +
            "('CLM002', 'EMP002', 'CAT002', '2024-01-10', 850.00, 'Flight tickets to conference', 'Approved', 'MGR001')," +
            "('CLM003', 'EMP003', 'CAT003', '2024-01-12', 425.00, 'Client lunch meeting', 'Approved', 'MGR002')," +
            "('CLM004', 'EMP004', 'CAT004', '2024-01-15', 320.00, 'Office supplies', 'Pending', NULL)," +
            "('CLM005', 'EMP005', 'CAT005', '2024-01-18', 1200.00, 'AWS certification course', 'Under Review', 'MGR001')," +
            "('CLM006', 'EMP006', 'CAT006', '2024-01-20', 5000.00, 'TechConf 2024 registration', 'Approved', 'MGR001')",

            // Approval data
            "INSERT IGNORE INTO approval(approvalId, claimId, approverId, approvalDate, amount, status, remarks) VALUES " +
            "('APR001', 'CLM001', 'MGR001', '2024-01-06', 2500.00, 'Approved', 'Approved as per policy')," +
            "('APR002', 'CLM002', 'MGR001', '2024-01-11', 850.00, 'Approved', 'Conference attendance approved')," +
            "('APR003', 'CLM003', 'MGR002', '2024-01-13', 425.00, 'Approved', 'Client entertainment approved')," +
            "('APR005', 'CLM006', 'MGR001', '2024-01-21', 5000.00, 'Approved', 'Conference registration approved')",

            // Reimbursement data
            "INSERT IGNORE INTO reimbursement(reimbId, claimId, employeeId, processedDate, amount, paymentMode, transactionRef, status) VALUES " +
            "('REIMB001', 'CLM001', 'EMP001', '2024-01-08', 2500.00, 'Bank Transfer', 'TXN20240108001', 'Processed')," +
            "('REIMB002', 'CLM002', 'EMP002', '2024-01-15', 850.00, 'Bank Transfer', 'TXN20240115001', 'Processed')," +
            "('REIMB003', 'CLM003', 'EMP003', '2024-01-18', 425.00, 'Cheque', 'CHQ20240118001', 'Processed')," +
            "('REIMB004', 'CLM006', 'EMP006', '2024-01-25', 5000.00, 'Bank Transfer', 'TXN20240125001', 'Processed')",

            // Account detail data
            "INSERT IGNORE INTO account_detail(bankname, accountno, employeeid, employeename, grosssal, natureofacount, dateofopen) VALUES " +
            "('State Bank of India', '12345678901234', 'EMP001', 'John Smith', 65000.00, 'Savings', '2020-01-15')," +
            "('HDFC Bank', '98765432109876', 'EMP002', 'Sarah Johnson', 72000.00, 'Current', '2021-03-20')," +
            "('ICICI Bank', '55667788990011', 'EMP003', 'Michael Brown', 68000.00, 'Savings', '2019-06-25')," +
            "('Axis Bank', '11223344556677', 'EMP004', 'Emily Davis', 58000.00, 'Savings', '2022-07-10')"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : insertStatements) {
                try {
                    stmt.executeUpdate(sql);
                } catch (SQLException e) {
                    // Ignore duplicate key errors
                    if (!e.getMessage().contains("Duplicate entry")) {
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Show error dialog
     */
    private static void showErrorDialog(String title, String message) {
        try {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("[" + title + "] " + message);
        }
    }

    /**
     * Show info dialog
     */
    public static void showInfoDialog(String title, String message) {
        try {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println("[" + title + "] " + message);
        }
    }
}
