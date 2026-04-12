# Expense Reimbursement System - Setup Guide

## 🚀 Quick Start

### macOS
```bash
chmod +x run.sh
./run.sh
```

### Windows
```cmd
run.bat
```

The application will automatically:
1. Check for MySQL installation
2. Verify MySQL is running
3. Create the database if needed
4. Create all required tables
5. Populate with realistic sample data
6. Launch the GUI

---

## 📋 Prerequisites

### macOS

#### 1. Install Java 21+
```bash
# Using Homebrew (recommended)
brew install java

# Verify installation
java -version
```

#### 2. Install MySQL
```bash
# Using Homebrew (recommended)
brew install mysql
brew services start mysql

# Verify installation
mysql --version
mysql -u root -e "SELECT version();"
```

#### 3. Optional: Install MySQL Workbench (GUI Database Tool)
```bash
brew install --cask mysql-workbench
```

### Windows

#### 1. Install Java 21+
1. Download from: https://www.oracle.com/java/technologies/javase-downloads.html
2. Run the installer and follow prompts
3. Ensure "Add to PATH" option is selected
4. Restart the system
5. Verify: Open CMD and run `java -version`

#### 2. Install MySQL Community Server
1. Download from: https://dev.mysql.com/downloads/mysql/
2. Run the installer
3. Choose "Developer Default" setup type
4. Configure MySQL Server:
   - Port: 3306
   - Windows Service: Yes (start automatically)
   - MySQL Root Password: **tiger** (or change it and update DatabaseInitializer.java)
5. Restart the system
6. Verify: Open CMD and run:
   ```cmd
   mysql -u root -ptiger -e "SELECT 1;"
   ```

#### 3. Optional: Install MySQL Workbench
1. Download from: https://www.mysql.com/products/workbench/
2. Run the installer and follow prompts

---

## 🔐 Default Credentials

### Application Login
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Admin User |
| john.smith | john123456 | Normal User |
| sarah.johnson | sarah123456 | Normal User |
| michael.brown | michael123456 | Normal User |
| emily.davis | emily123456 | Normal User |
| robert.wilson | robert123456 | Normal User |

### Database
| Property | Value |
|----------|-------|
| Host | localhost |
| Port | 3306 |
| Database | expanses |
| Username | root |
| Password | tiger |

**⚠️ IMPORTANT:** Change these credentials in production!

---

## 📊 Sample Data

The application includes realistic sample data for testing:

### Users (6 employees)
- Various roles: Senior Developer, Project Manager, Business Analyst, etc.
- Contact information and salary data

### Managers (5 managers)
- Department assignments: Engineering, Finance, Operations, HR, Sales
- Approval authority for expense claims

### Expense Categories (9 categories)
- Travel (Accommodation & Transportation)
- Meals & Entertainment
- Office Supplies
- Training & Development
- Conference & Events
- Mobile & Internet
- Equipment & Hardware
- Utilities & Rent

### Sample Transactions
- 12 expense claims
- 10 approvals
- 7 reimbursement payments
- 8 employee accounts
- 8 customer records
- 10 audit log entries

---

## 🏗️ Database Structure

### Tables Created
1. **login** - User authentication (6 users)
2. **employee** - Employee master data (8 employees)
3. **manager** - Approval managers (5 managers)
4. **expensecategory** - Expense types (9 categories)
5. **claim** - Submitted expense claims (12 claims)
6. **approval** - Manager approvals (10 approvals)
7. **reimbursement** - Payment records (7 reimbursements)
8. **account_detail** - Bank accounts (8 accounts)
9. **auditlog** - System audit trail (10 entries)
10. **customerdetail** - Guest/customer info (8 customers)
11. **RecurringExpanse** - Budget tracking (8 records)

### Key Features
- **Foreign keys** for data integrity
- **Timestamps** for tracking
- **Indexing** for performance
- **UTF8MB4** encoding for international characters
- **InnoDB** engine for transactions

---

## 🔧 Manual Database Setup (if automatic setup fails)

### Connect to MySQL
```bash
# macOS/Linux
mysql -u root -ptiger

# Windows CMD
mysql -u root -ptiger
```

### Create Database
```sql
CREATE DATABASE IF NOT EXISTS expanses CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE expanses;
```

### Load Sample Data
```bash
# macOS/Linux
mysql -u root -ptiger expanses < database_init.sql

# Windows CMD
mysql -u root -ptiger expanses < database_init.sql
```

### Verify
```sql
USE expanses;
SHOW TABLES;
SELECT COUNT(*) as user_count FROM login;
SELECT COUNT(*) as claim_count FROM claim;
```

---

## 🚨 Troubleshooting

### "MySQL not found in PATH"
**macOS:**
```bash
brew install mysql
brew services start mysql
```

**Windows:**
1. Reinstall MySQL and select "Add MySQL to System PATH"
2. Restart computer
3. Re-run `run.bat`

### "Cannot connect to MySQL"
```bash
# Check if MySQL is running
# macOS
brew services list

# Windows
net start | findstr /i mysql

# Start MySQL if stopped
# macOS
brew services start mysql

# Windows
net start MySQL80
```

### "Wrong password" or "Access denied"
Verify MySQL credentials:
```bash
mysql -u root -ptiger -e "SELECT 1;"
```

Change default password if needed:
```bash
mysql -u root -ptiger -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';"
```

Then update `DatabaseInitializer.java`:
```java
private static final String DB_PASS = "newpassword";
```

### "Compilation failed"
1. Ensure Java is installed: `java -version`
2. Ensure MySQL JDBC driver exists: `lib/mysql-connector-j-9.6.0.jar`
3. Delete bin folder and rebuild
4. Check Java source files for syntax errors

### "Port 3306 already in use"
```bash
# macOS - Find process using port 3306
lsof -i :3306

# Windows - Find process using port 3306
netstat -ano | findstr :3306

# Kill the process (use caution)
kill -9 <PID>  # macOS
taskkill /PID <PID> /F  # Windows
```

---

## 📁 Project Structure

```
expensereimbursementsystem/
├── src/                          # Java source files
│   ├── App.java                 # Entry point
│   ├── DatabaseInitializer.java # Database setup
│   ├── SignUpWindow.java        # Registration UI
│   ├── Login.java               # Login UI
│   ├── MdiForm.java             # Main menu
│   └── ... (24+ other classes)
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── bin/                          # Compiled classes (generated)
├── build.sh                      # macOS build script
├── build.bat                     # Windows build script
├── run.sh                        # macOS launcher
├── run.bat                       # Windows launcher
├── app.jar                       # Compiled application (generated)
├── manifest.txt                  # JAR manifest
├── database_init.sql            # SQL schema & sample data
├── README.md                     # This file
└── MyIcon.icns                   # Application icon

```

---

##  Usage

### First Time Launch
1. Run `run.sh` (macOS) or `run.bat` (Windows)
2. Wait for database initialization to complete
3. Sign up as a new user or login with sample credentials
4. Navigate to different modules using the main menu

### Default Flow
1. **Login Window** → Enter credentials
2. **Main Menu (MDI Form)** → Access Modules
   - Employee Management
   - Expense Claims
   - Approvals
   - Reimbursements
   - Reports
   - Audit Logs
3. **CRUD Operations** → Add/Edit/Delete data
4. **Query/Report** → Search and generate reports

### Sample Testing Scenarios

#### Scenario 1: Submit Expense Claim
1. Login as `john.smith` / `john123456`
2. Navigate to "New Claim"
3. Fill in expense details
4. Submit claim

#### Scenario 2: Approve Claim
1. Login as `admin` / `admin123`
2. Navigate to "Approvals"
3. View pending claims
4. Approve/Reject with remarks

#### Scenario 3: Process Reimbursement
1. Login as `admin`
2. Navigate to "Reimbursement"
3. Select approved claim
4. Process payment with bank transfer details

#### Scenario 4: Generate Report
1. Navigate to "Reports"
2. Select report type (Employee, Expense Category, etc.)
3. Filter by criteria
4. View/print results

---

## 🛡️ Security Notes

### Before Production Deployment

1. **Change Default Passwords**
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'strong_password';
   UPDATE login SET password = 'hashed_password' WHERE username = 'admin';
   ```

2. **Enable Password Hashing**
   - Install BCrypt library
   - Hash passwords in DatabaseInitializer.java
   - Update Login.java to use hashing

3. **Externalize Configuration**
   - Create `config.properties` file
   - Move database credentials out of code
   - Use environment variables

4. **Enable SSL/TLS**
   - Configure MySQL with SSL
   - Update JDBC URL: `jdbc:mysql://localhost:3306/expanses?useSSL=true&requireSSL=true`

5. **Database Backups**
   ```bash
   mysqldump -u root -p expanses > backup_$(date +%Y%m%d).sql
   ```

6. **Audit Logging**
   - Monitor auditlog table regularly
   - Set up automated log exports
   - Implement retention policies

---

## 📚 Additional Resources

- **Java Documentation**: https://docs.oracle.com/javase/21/
- **MySQL Documentation**: https://dev.mysql.com/doc/
- **JDBC Documentation**: https://dev.mysql.com/doc/connector-j/
- **Swing UI Framework**: https://docs.oracle.com/javase/tutorial/swing/

---

## 📞 Support

### For Database Issues
1. Check MySQL is running
2. Verify credentials
3. Run: `mysql -u root -ptiger -e "SHOW TABLES FROM expanses;"`
4. Check database_init.sql for errors

### For Application Issues
1. Check Java version: `java -version`
2. Ensure MySQL driver exists: `lib/mysql-connector-j-9.6.0.jar`
3. Delete `bin` folder and rebuild
4. Check console output for error messages

### Review Documentation
- [CODEBASE_ANALYSIS.md](CODEBASE_ANALYSIS.md) - Complete code overview
- [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) - Database design details
- [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Developer quick reference

---

## 📝 License & Credits

This is a training/demo project for Expense Reimbursement Management System.

**Built with:**
- Java Swing (UI Framework)
- MySQL (Database)
- JDBC (Database Connection)

**Components Included:**
- 27 Java classes
- 11 database tables
- Comprehensive sample data
- Automatic database initialization
- Cross-platform launchers (Windows & macOS)

---

## ✅ Checklist for First Run

- [ ] Java 21+ installed and in PATH
- [ ] MySQL installed and running
- [ ] Changed to project directory
- [ ] MySQL root password is "tiger" (or updated in DatabaseInitializer.java)
- [ ] lib/mysql-connector-j-9.6.0.jar exists
- [ ] Ran `chmod +x run.sh` (macOS only)
- [ ] Executed `./run.sh` (macOS) or `run.bat` (Windows)
- [ ] Application launched successfully
- [ ] Can login with `admin`/`admin123`
- [ ] Sample data appears in tables

---

**Last Updated:** April 13, 2026  
**Version:** 1.0  
**Status:** Ready for Testing
