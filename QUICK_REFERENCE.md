# Quick Reference & Developer Guide

## 🚀 Quick Start

### Prerequisites
- Java 21+
- MySQL 8.0+
- macOS (for DMG packaging)

### Setup Instructions

1. **Create Database**
   ```bash
   mysql -u root -p
   CREATE DATABASE expanses CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Compile**
   ```bash
   ./build.sh
   ```

3. **Run**
   ```bash
   java -cp "lib/mysql-connector-j-9.6.0.jar:bin" App
   ```

4. **Create DMG Package (macOS)**
   ```bash
   ./build.sh
   # Output: output/ExpenseReimbursements-1.0.dmg
   ```

---

## 📊 Class Hierarchy

```
java.lang.Object
├── java.awt.Component
│   └── java.awt.Container
│       ├── java.awt.Window
│       │   ├── java.awt.Frame
│       │   │   ├── App (main entry)
│       │   │   ├── Login extends JFrame
│       │   │   ├── SignUpWindow extends JFrame
│       │   │   ├── MdiForm extends JFrame
│       │   │   ├── *Detail extends JFrame OR extends BaseCrudWindow
│       │   │   │   ├── EmployeeDetail extends JFrame
│       │   │   │   ├── AccountDetail extends JFrame
│       │   │   │   ├── BookingDetail extends BaseCrudWindow
│       │   │   │   ├── DonerDetail extends BaseCrudWindow
│       │   │   │   ├── CustomerDetail extends JFrame
│       │   │   │   ├── EnquiryDetail extends BaseCrudWindow
│       │   │   │   ├── SalaryDetail extends BaseCrudWindow
│       │   │   │   ├── InventoryDetail extends BaseCrudWindow
│       │   │   │   ├── TaskDetail extends BaseCrudWindow
│       │   │   │   └── TransactionDetail extends JFrame
│       │   │   ├── *Query extends JFrame OR extends BaseQueryWindow
│       │   │   │   ├── EmployeeSectionQuery extends JFrame
│       │   │   │   ├── AccountSectionQuerry extends JFrame
│       │   │   │   ├── EnquirySectionQuery extends BaseQueryWindow
│       │   │   │   └── BookingSectionQuerry extends BaseQueryWindow
│       │   │   ├── *Report extends JFrame OR extends BaseReportWindow
│       │   │   │   ├── EmployeeSectionReport extends JFrame
│       │   │   │   ├── AccountSectionReport extends JFrame
│       │   │   │   └── DonerSectionReport extends BaseReportWindow
│       │   │   └── Utility Windows
│       │   │       ├── QueryWindows (factory)
│       │   │       └── ReportWindows (factory)
│       │   └── Dialog variants
│       └── JPanel, etc.

├── javax.swing.table.AbstractTableModel
│   └── ResultSetTableModel

└── Utility Classes
    └── AppMenuBar (nested in MdiForm)
```

---

## 🎯 Module Quick Reference

### Authentication Layer (2 days to master)
| Class | Database | Purpose | Navigation |
|-------|----------|---------|-----------|
| `App` | - | Entry point | → SignUpWindow |
| `Login` | login | Authenticate user | → MdiForm / SignUpWindow |
| `SignUpWindow` | login | Register new user | → Login / MdiForm |

### Main Navigation (1 day)
| Class | Purpose | Menus |
|-------|---------|-------|
| `MdiForm` | Main window with menu bar | User Module, Modules, Queries, Reports |
| `AppMenuBar` | Menu factory (nested) | Dynamic menu creation via reflection |

### CRUD Detail Windows (3 days - 10 windows)
| Window | Table | Type | Status |
|--------|-------|------|--------|
| `EmployeeDetail` | employee | JFrame | ✅ Complete |
| `AccountDetail` | account_detail | JFrame | ✅ Complete |
| `BookingDetail` | manager | BaseCrudWindow | ✅ Complete |
| `DonerDetail` | expensecategory | BaseCrudWindow | ✅ Complete |
| `CustomerDetail` | customerdetail | JFrame | ✅ Complete |
| `EnquiryDetail` | claim | BaseCrudWindow | ✅ Complete |
| `SalaryDetail` | reimbursement | BaseCrudWindow | ✅ Complete |
| `InventoryDetail` | approval | BaseCrudWindow | ✅ Complete |
| `TaskDetail` | auditlog | BaseCrudWindow | ✅ Complete (read-only) |
| `TransactionDetail` | RecurringExpanse | JFrame | ⚠️ Separate DB |

### Query/Search Windows (2 days - 4 windows)
| Window | Table | Search Type | Status |
|--------|-------|------------|--------|
| `EmployeeSectionQuery` | employee | Radio buttons | ✅ Complete |
| `AccountSectionQuerry` | account_detail | Radio buttons | ✅ Complete |
| `EnquirySectionQuery` | claim | BaseQueryWindow | ✅ Complete |
| `BookingSectionQuerry` | manager | BaseQueryWindow | ✅ Complete |

### Report Windows (2 days - 3+ windows)
| Window | Source | Format | Status |
|--------|--------|--------|--------|
| `EmployeeSectionReport` | employee | Text + Print | ✅ Complete |
| `AccountSectionReport` | account_detail | Text + Print | ✅ Complete |
| `DonerSectionReport` | expensecategory | Text + Print | ✅ Complete |

### Base Classes (UI Templates - 1 day)
| Class | Purpose | Methods | Used By |
|-------|---------|---------|---------|
| `BaseCrudWindow` | Template for CRUD forms | connectDB(), loadTable(), make*() | Most Detail windows |
| `BaseQueryWindow` | Template for search forms | Constructor-driven field mapping | Query windows |
| `BaseReportWindow` | Template for reports | Format output, generate, print | Report windows |

### Utility Classes (1 day)
| Class | Purpose | Usage |
|-------|---------|-------|
| `ResultSetTableModel` | Convert JDBC → Swing | `table.setModel(new ResultSetTableModel(rs))` |
| `AppMenuBar` | Create menu bar with reflection | Called by each window |
| `QueryWindows` | Query factory | Base for query implementations |
| `ReportWindows` | Report factory | Base for report implementations |

---

## 🗃️ File Structure & Compilation

### Before Build
```
src/
├── App.java
├── Login.java
├── SignUpWindow.java
├── MdiForm.java (contains AppMenuBar)
├── BaseCrudWindow.java
├── BaseQueryWindow.java
├── BaseReportWindow.java
├── ResultSetTableModel.java
├── QueryWindows.java
├── ReportWindows.java
├── *Detail.java (10 files)
├── *Query.java (4 files)
├── *Report.java (3 files)
└── assets/
    └── icons/
```

### After Build
```
bin/
├── App.class
├── Login.class
├── SignUpWindow.class
├── MdiForm.class
├── MdiForm$AppMenuBar.class
├── BaseCrudWindow.class
├── BaseQueryWindow.class
├── BaseReportWindow.class
├── ResultSetTableModel.class
├── QueryWindows.class
├── ReportWindows.class
├── *Detail.class (10 files)
├── *Query.class (4 files)
├── *Report.class (3 files)
└── assets/
```

### JAR Structure
```
app.jar
├── META-INF/
│   └── MANIFEST.MF (Main-Class: App)
├── [all .class files]
└── assets/
```

---

## 🔌 Common Code Patterns

### Pattern 1: Database Connection
```java
protected Connection con = null;
protected PreparedStatement pst = null;
protected ResultSet rs = null;

protected void connectDB() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/expanses?useSSL=false", 
            "root", "tiger");
    } catch (Exception ex) { /* error handling */ }
}
```

### Pattern 2: Load Data into Table
```java
protected void loadTable(String sql) {
    try {
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        String[] names = new String[cols];
        for (int i = 0; i < cols; i++) 
            names[i] = meta.getColumnLabel(i + 1);
        
        tableModel = new DefaultTableModel(names, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i = 0; i < cols; i++) 
                row[i] = rs.getObject(i + 1);
            tableModel.addRow(row);
        }
        table.setModel(tableModel);
    } catch (SQLException ex) { /* error handling */ }
}
```

### Pattern 3: CRUD Operations
```java
// CREATE
pst = con.prepareStatement(
    "INSERT INTO tablename(col1, col2, col3) VALUES(?,?,?)");
pst.setString(1, value1);
pst.setString(2, value2);
pst.setDouble(3, value3);
pst.executeUpdate();

// READ (shown in loadTable pattern above)

// UPDATE
pst = con.prepareStatement(
    "UPDATE tablename SET col1=?, col2=? WHERE id=?");
pst.setString(1, value1);
pst.setString(2, value2);
pst.setInt(3, idValue);
pst.executeUpdate();

// DELETE
pst = con.prepareStatement("DELETE FROM tablename WHERE id=?");
pst.setInt(1, idValue);
pst.executeUpdate();
```

### Pattern 4: Form Validation
```java
if (txtField.getText().trim().isEmpty()) {
    showError("Field cannot be empty");
    return;
}
if (!isValidEmail(email)) {
    showError("Invalid email format");
    return;
}
if (amount <= 0) {
    showError("Amount must be positive");
    return;
}
```

### Pattern 5: Dynamic Window Opening
```java
// Reflective class loading (used in MdiForm)
private static void openClass(String simpleName, JFrame host) {
    try {
        Class<?> cls = Class.forName(simpleName);
        JFrame form = (JFrame) cls.getDeclaredConstructor().newInstance();
        form.setVisible(true);
        host.dispose();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(host,
            simpleName + " not available.\n(" + ex.getMessage() + ")");
    }
}
```

---

## 🐛 Common Issues & Solutions

### Issue 1: Database Connection Failed
**Symptom:** "DB connection failed: com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException"

**Solutions:**
```bash
# Check MySQL is running
mysql -u root -p

# Verify database exists
mysql -u root -p -e "SHOW DATABASES;"

# Create tables if missing
mysql -u root -p expanses < schema.sql

# Update DB credentials in source (hardcoded - not ideal)
# Change in App.java, Login.java, SignUpWindow.java, etc.
```

### Issue 2: "Main class App not found"
**Symptom:** When running JAR: "Error: Could not find or load main class App"

**Solution:**
Check `manifest.txt`:
```
Main-Class: App
Class-Path: lib/mysql-connector-j-9.6.0.jar
```

Rebuild JAR:
```bash
jar cfm app.jar manifest.txt -C bin .
```

### Issue 3: No JDBC Driver
**Symptom:** "No suitable driver found for jdbc:mysql://localhost:3306/expanses"

**Solutions:**
```bash
# Ensure JAR is in classpath
javac -cp "lib/mysql-connector-j-9.6.0.jar" *.java
java -cp "lib/mysql-connector-j-9.6.0.jar:bin" App

# Or ensure JAR is in input/lib when building
cp lib/mysql-connector-j-9.6.0.jar input/lib/
```

### Issue 4: Class Loader Issues
**Symptom:** "ClassNotFoundException: com.mysql.cj.jdbc.Driver"

**Solution:**
```java
// Verify driver can be found
try {
    Class.forName("com.mysql.cj.jdbc.Driver");
} catch (ClassNotFoundException e) {
    System.out.println("Driver not found. Check classpath.");
    e.printStackTrace();
}
```

---

## 📈 Development Roadmap

### Phase 1: Understand (3-5 days)
- [ ] Read CODEBASE_ANALYSIS.md
- [ ] Read DATABASE_SCHEMA.md
- [ ] Trace execution flow from App → Login → MdiForm
- [ ] Understand CRUD operations in one Detail window
- [ ] Review BaseC rudWindow pattern

### Phase 2: Mini-Modification (2-3 days)
- [ ] Add a new field to an existing table
- [ ] Update corresponding Detail window
- [ ] Test insert/read/update/delete
- [ ] Verify change is logged in auditlog

### Phase 3: New Feature (5-7 days)
- [ ] Design new table schema
- [ ] Create Detail window class extending BaseCrudWindow
- [ ] Create Query window for searching
- [ ] Create Report window
- [ ] Add menu items to MdiForm
- [ ] Test full workflow

### Phase 4: Testing (3-5 days)
- [ ] Manual testing of all operations
- [ ] Edge case testing (empty fields, duplicates, etc.)
- [ ] Database integrity checks
- [ ] Performance testing with large datasets

### Phase 5: Production (2-3 days)
- [ ] Externalize configuration
- [ ] Add logging framework
- [ ] Security audit
- [ ] Package as DMG
- [ ] Document changes

---

## 🧪 Testing Scenarios

### Test Case 1: Complete Expense Claim Workflow
```
1. User A logs in (Normal User)
2. Submits expense claim (₹5,000, Travel category)
   → Verify: claim table entry created, status = 'Pending'
3. Manager logs in as User B (Admin User)
4. Approves claim with ₹4,500 (partial)
   → Verify: approval table entry, claim status = 'Approved'
5. Finance processes reimbursement
   → Verify: reimbursement entry, status = 'Processed'
6. Check audit log
   → Verify: All transactions logged with old/new values
```

### Test Case 2: Data Validation
```
1. Try to create claim with negative amount
   → Must reject with error message
2. Try to use non-existent expense category
   → Database constraint should prevent
3. Try duplicate claim ID
   → Unique constraint should prevent
4. Try empty employee name
   → Insert should fail with error
```

### Test Case 3: Concurrent Access
```
1. Two users try to approve same claim simultaneously
   → Last update wins (currently no locking)
2. One user updates, other user queries
   → Should see dirty read (no transaction isolation)
```

### Test Case 4: Report Generation
```
1. Generate Employee Section Report
   → Verify: All employee records included
   → Verify: Formatting is correct
   → Verify: Print functionality works
2. Generate expense claim report
   → Verify: Filtered by date range
   → Verify: Aggregations are correct
```

---

## 🔒 Security Considerations

### Current Issues
- ⚠️ Hardcoded database credentials in source
- ⚠️ No password hashing (plaintext in database)
- ⚠️ No SQL parameterization in some queries
- ⚠️ No session management
- ⚠️ No input validation on some fields
- ⚠️ Audit log stores sensitive data

### Recommendations
```java
// 1. Externalize credentials
// config/app.properties
mysql.username=${DB_USER}
mysql.password=${DB_PASS}

// 2. Hash passwords
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
String hashedPassword = new BCryptPasswordEncoder().encode(plainPassword);

// 3. Use parameterized queries (already doing in most places)
pst = con.prepareStatement("SELECT * FROM employee WHERE id = ?");
pst.setInt(1, id);

// 4. Add session management
HttpSession session = request.getSession();
session.setAttribute("userId", login.getUserId());

// 5. Input validation
if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
    throw new ValidationException("Invalid username format");
}

// 6. Logging framework
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
private static Logger logger = LoggerFactory.getLogger(App.class);
logger.info("User {} logged in", username);
```

---

## 📚 Learning Resources

### For This Codebase
1. Start: [CODEBASE_ANALYSIS.md](CODEBASE_ANALYSIS.md)
2. Data Model: [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)
3. Deep Dive: Read BaseCrudWindow.java (the template)
4. Experiment: Modify DonerDetail.java (simplest example)

### General Java/Swing
- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)
- [MySQL JDBC Driver Docs](https://dev.mysql.com/doc/connector-j/en/)

### Design Patterns Used
- Template Method Pattern (Base* classes)
- Factory Pattern (AppMenuBar, reflective loading)
- MVC Pattern (Models, Views, Controllers implied)
- DAO Pattern (Database access methods)

---

## 🚨 Known Limitations

1. **No Transaction Support** - If error occurs mid-workflow, partial updates possible
2. **No Connection Pooling** - Creates new connection per operation (slow)
3. **No Concurrent User Handling** - No row locking, last writer wins
4. **Date Storage** - Uses VARCHAR instead of DATE type (hard to query)
5. **No Password Encryption** - Passwords stored in plaintext
6. **Hardcoded Configuration** - Database credentials in source code
7. **No Pagination** - Loads entire table into memory
8. **Limited Error Handling** - Generic exception messages
9. **No Input Validation** - Some fields not validated before insert
10. **Memory Leaks** - ResultSet/PreparedStatement not always closed in finally blocks

---

## 📝 SQL Initialization Script

Save as `init_schema.sql` for quick setup:

```sql
CREATE DATABASE expanses CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE expanses;

CREATE TABLE login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Role VARCHAR(50)
) ENGINE=InnoDB;

CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId VARCHAR(20) NOT NULL UNIQUE,
    employeeName VARCHAR(100) NOT NULL,
    dob VARCHAR(20),
    companyId VARCHAR(20),
    doj VARCHAR(20),
    salary DECIMAL(12,2),
    postName VARCHAR(50),
    address VARCHAR(200),
    pincode VARCHAR(10),
    contactNo VARCHAR(20)
) ENGINE=InnoDB;

-- [... rest of CREATE TABLE statements ...]

-- Add indices for performance
CREATE INDEX idx_claim_empid ON claim(employeeId);
CREATE INDEX idx_claim_status ON claim(status);
CREATE INDEX idx_approval_claimid ON approval(claimId);
CREATE INDEX idx_reimbursement_empid ON reimbursement(employeeId);
CREATE INDEX idx_auditlog_userid ON auditlog(userId);
CREATE INDEX idx_auditlog_timestamp ON auditlog(timestamp DESC);

-- Insert test data
INSERT INTO login VALUES 
(1, 'Raj Kumar', 'raj_kumar', 'password123', 'raj@company.com', 'Normal User'),
(2, 'Admin User', 'admin', 'admin123', 'admin@company.com', 'Admin User');

-- ... [test data for other tables] ...
```

Run with:
```bash
mysql -u root -p < init_schema.sql
```

---

## 🎓 Summary

**Learning Path: 2-3 weeks**
1. **Days 1-2:** Understand architecture (read analysis docs)
2. **Days 3-5:** Set up, compile, run application
3. **Days 6-10:** Modify existing module (add field, validation)
4. **Days 11-15:** Create new feature (new table/window)
5. **Days 16-21:** Test, optimize, document

**Difficulty Levels:**
- 🟢 **Trivial** (1 hour): Change field label, add validation rule
- 🟡 **Easy** (1-2 days): Add new field to existing table
- 🟠 **Medium** (3-5 days): Create new Detail window
- 🔴 **Hard** (1-2 weeks): Implement completely new workflow with multiple windows

---

*Quick Reference Guide - Expense Reimbursement System v1.0*  
*For detailed information, see CODEBASE_ANALYSIS.md and DATABASE_SCHEMA.md*
