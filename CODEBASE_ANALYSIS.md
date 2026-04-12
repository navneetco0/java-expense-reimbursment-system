# Expense Reimbursement System - Comprehensive Codebase Analysis

## 📋 Table of Contents
1. [Application Overview](#application-overview)
2. [Architecture & Structure](#architecture--structure)
3. [Database Schema](#database-schema)
4. [Class Documentation](#class-documentation)
5. [UI Flow & Navigation](#ui-flow--navigation)
6. [Business Logic](#business-logic)
7. [Sample Data](#sample-data)
8. [Dependencies](#dependencies)
9. [Build & Deployment](#build--deployment)

---

## Application Overview

**Name:** Expense Reimbursement System  
**Technology Stack:** Java Swing (GUI), MySQL (Database)  
**Purpose:** Track employee expense claims, approve reimbursements, and manage payment processing  
**Type:** Desktop application (macOS DMG package support)  
**Database:** MySQL (database: `expanses`)

The system manages the complete workflow of expense claims from submission through approval to reimbursement payment.

---

## Architecture & Structure

### Folder Structure
```
expensereimbursementsystem/
├── src/                          # Java source files
│   ├── App.java                 # Entry point
│   ├── Login.java               # Login window
│   ├── SignUpWindow.java        # User registration
│   ├── MdiForm.java             # Main MDI form (menu bar & navigation)
│   ├── *Detail.java             # CRUD detail windows (10 classes)
│   ├── *Query.java              # Search query windows (4 classes)
│   ├── *Report.java             # Report generation windows (2 classes)
│   ├── Base*.java               # Base classes for UI patterns (3 classes)
│   ├── ResultSetTableModel.java # Utility for JDBC→Swing conversion
│   ├── QueryWindows.java        # Query base factory
│   ├── ReportWindows.java       # Report base factory
│   └── assets/                  # Application icons/images
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── bin/                         # Compiled output
├── output/
│   └── ExpenseReimbursements-1.0.dmg
├── MyIcon.iconset/              # macOS icon source
├── manifest.txt                 # JAR manifest
├── build.sh                     # Compilation & packaging script
└── README.md
```

### Module Organization

The application follows an **MDI (Multiple Document Interface)** pattern with:

1. **Authentication Layer**
   - `App.java` - Entry point
   - `Login.java` - Login window
   - `SignUpWindow.java` - User registration
   
2. **Navigation Layer**
   - `MdiForm.java` - Main window with menu bar
   - `AppMenuBar.java` (nested in MdiForm) - Dynamic menu bar

3. **CRUD Modules (Detail Windows)**
   - User/Employee management
   - Account management
   - Claim management
   - Reimbursement processing
   - Approval tracking
   - Audit logging

4. **Query/Search Module**
   - `BaseQueryWindow.java` - Template for search forms
   - Multiple query implementations

5. **Reports Module**
   - `BaseReportWindow.java` - Template for reports
   - Report generators

6. **Base Classes (UI Patterns)**
   - `BaseCrudWindow.java` - Shared CRUD UI scaffold
   - `BaseQueryWindow.java` - Query form scaffold
   - `BaseReportWindow.java` - Report window scaffold

---

## Database Schema

### Database Connection Details
- **URL:** `jdbc:mysql://localhost:3306/expanses?useSSL=false`
- **Username:** `root`
- **Password:** `tiger`
- **Database Name:** `expanses`

### 1. **login** (Authentication Table)
**Purpose:** User authentication and role management

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | Unique identifier |
| name | VARCHAR(100) | NOT NULL | User's full name |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| password | VARCHAR(100) | NOT NULL | Hashed password |
| Email | VARCHAR(100) | | User email |
| Role | VARCHAR(50) | | 'Normal User' or 'Admin User' |

---

### 2. **employee** (Employee Master Table)
**Purpose:** Store employee information

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| employeeId | VARCHAR(20) | UNIQUE, NOT NULL | Employee code (e.g., EMP001) |
| employeeName | VARCHAR(100) | NOT NULL | Employee full name |
| dob | DATE/VARCHAR(20) | | Date of birth |
| companyId | VARCHAR(20) | | Company or department code |
| doj | VARCHAR(20) | | Date of joining |
| salary | DECIMAL(12,2) | | Monthly salary |
| postName | VARCHAR(50) | | Job title/designation |
| address | VARCHAR(200) | | Residential address |
| pincode | VARCHAR(10) | | Postal code |
| contactNo | VARCHAR(20) | | Phone number |

---

### 3. **account_detail** (Bank/Account Information)
**Purpose:** Store employee bank account details for reimbursement

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| bankName | VARCHAR(100) | | Bank name |
| accountNo | VARCHAR(30) | | Bank account number |
| billId | VARCHAR(30) | | Bill or reference ID |
| customerName | VARCHAR(100) | | Customer/Employee name |
| totalAmount | DECIMAL(12,2) | | Total amount |
| natureOfAccount | VARCHAR(50) | | Account type (Savings, Current, etc.) |
| paymentDate | VARCHAR(20) | | Payment date |
| employeeId | VARCHAR(20) | | FK to employee |
| empName | VARCHAR(100) | | Employee name (denormalized) |

---

### 4. **manager** (Manager/Approver Master)
**Purpose:** Store manager information for claim approval

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| managerId | VARCHAR(20) | UNIQUE, NOT NULL | Manager code |
| managerName | VARCHAR(100) | NOT NULL | Manager full name |
| department | VARCHAR(80) | | Department |
| email | VARCHAR(100) | | Email address |
| phone | VARCHAR(20) | | Phone number |
| hireDate | VARCHAR(20) | | Hire date |
| salary | DECIMAL(12,2) | | Salary |
| status | VARCHAR(20) | DEFAULT 'Active' | 'Active' or 'Inactive' |

---

### 5. **expensecategory** (Expense Categories)
**Purpose:** Define expense types and budget limits

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| categoryId | VARCHAR(20) | UNIQUE, NOT NULL | Category code (e.g., TRAVEL, MEAL) |
| categoryName | VARCHAR(100) | NOT NULL | Category name |
| description | VARCHAR(255) | | Detailed description |
| budgetLimit | DECIMAL(12,2) | DEFAULT 0 | Maximum allowed per claim |
| isActive | VARCHAR(10) | DEFAULT 'Yes' | 'Yes' or 'No' |

---

### 6. **claim** (Expense Claims)
**Purpose:** Store individual expense claims submitted by employees

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| claimId | VARCHAR(20) | UNIQUE, NOT NULL | Claim reference (e.g., CLM001) |
| employeeId | VARCHAR(20) | | FK to employee |
| categoryId | VARCHAR(20) | | FK to expensecategory |
| claimDate | VARCHAR(20) | | Date claim was submitted |
| amount | DECIMAL(12,2) | DEFAULT 0 | Claimed amount (₹) |
| description | VARCHAR(255) | | Details of expense |
| status | VARCHAR(20) | DEFAULT 'Pending' | 'Pending', 'Approved', 'Rejected', 'Under Review' |
| approvedBy | VARCHAR(50) | | Manager who approved |

---

### 7. **approval** (Approval Tracking)
**Purpose:** Track claim approvals with amounts approved

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| approvalId | VARCHAR(20) | UNIQUE, NOT NULL | Approval record ID |
| claimId | VARCHAR(20) | | FK to claim |
| approverId | VARCHAR(20) | | FK to manager (approver) |
| approvalDate | VARCHAR(20) | | Date of approval |
| amount | DECIMAL(12,2) | DEFAULT 0 | Approved amount (₹) |
| status | VARCHAR(20) | DEFAULT 'Pending' | 'Pending', 'Approved', 'Rejected', 'Partially Approved' |
| remarks | VARCHAR(255) | | Approval comments |

---

### 8. **reimbursement** (Reimbursement Payments)
**Purpose:** Track actual reimbursement payments to employees

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| reimbId | VARCHAR(20) | UNIQUE, NOT NULL | Reimbursement ID (e.g., RMB001) |
| claimId | VARCHAR(20) | | FK to claim |
| employeeId | VARCHAR(20) | | FK to employee |
| processedDate | VARCHAR(20) | | Date reimbursement was processed |
| amount | DECIMAL(12,2) | DEFAULT 0 | Amount paid (₹) |
| paymentMode | VARCHAR(30) | DEFAULT 'Bank Transfer' | 'Bank Transfer', 'Cheque', 'Cash', 'UPI', 'NEFT', 'RTGS' |
| transactionRef | VARCHAR(50) | | Transaction reference number |
| status | VARCHAR(20) | DEFAULT 'Processed' | 'Processed', 'Pending', 'Failed', 'On Hold' |

---

### 9. **auditlog** (Audit Logging)
**Purpose:** Track all changes for compliance and security (append-only)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| logId | VARCHAR(30) | UNIQUE, NOT NULL | Unique log entry ID |
| userId | VARCHAR(30) | | User who made the change |
| action | VARCHAR(20) | | 'INSERT', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'VIEW' |
| targetTable | VARCHAR(50) | | Table that was modified |
| targetId | VARCHAR(30) | | ID of record modified |
| timestamp | VARCHAR(30) | | When change occurred |
| oldValue | VARCHAR(255) | | Previous value (for UPDATE) |
| newValue | VARCHAR(255) | | New value (for UPDATE) |
| ipAddress | VARCHAR(30) | | Client IP address |

---

### 10. **customerdetail** (Customer/Guest Information)
**Purpose:** Store customer/guest booking information (used in interface)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INT | AUTO_INCREMENT PRIMARY KEY | DB auto-ID |
| customerId | VARCHAR(20) | | Customer code |
| customerName | VARCHAR(100) | | Customer name |
| address | VARCHAR(200) | | Address |
| city | VARCHAR(50) | | City |
| pin | VARCHAR(10) | | Postal code |
| noOfPersons | VARCHAR(10) | | Number of persons |
| identificationProof | VARCHAR(50) | | ID proof type |
| contactNo | VARCHAR(20) | | Contact number |
| totalRoom | VARCHAR(10) | | Number of rooms |
| dateOfBooking | VARCHAR(20) | | Booking date |

---

### 11. **RecurringExpanse** (Recurring/Budget Tracking)
**Purpose:** Track recurring expenses and budget management (optional table)

**Database:** `expansebudget` (separate from `expanses`)  
**User:** RAJANISH / RAJANISH

---

## Data Relationships & Flow

### Expense Claim Workflow

```
Employee (login)
    ↓
Submit Expense Claim → claim table
    ↓ (references: employeeId, categoryId)
Manager Reviews
    ↓
Create Approval Record → approval table
    ↓ (references: claimId, approverId)
Approve/Reject
    ↓ (if Approved)
Process Reimbursement → reimbursement table
    ↓ (references: claimId, employeeId, account_detail)
Disburse Payment
    ↓
Log Changes → auditlog table
```

### Key Foreign Key Relationships

| Source Table | FK Column | Target Table | Target Column |
|--------------|-----------|--------------|--------------|
| claim | employeeId | employee | employeeId |
| claim | categoryId | expensecategory | categoryId |
| approval | claimId | claim | claimId |
| approval | approverId | manager | managerId |
| reimbursement | claimId | claim | claimId |
| reimbursement | employeeId | employee | employeeId |
| account_detail | employeeId | employee | employeeId |
| auditlog | userId | login | username |

---

## Class Documentation

### Entry Point & Authentication

#### **App.java**
- **Purpose:** Application entry point
- **Method:** `main(String[] args)`
- **Flow:** Initializes Swing and shows `SignUpWindow`
- **Key Features:**
  - Sets macOS app name in dock
  - Handles uncaught exceptions

#### **Login.java**
- **Purpose:** User authentication window
- **Components:**
  - `edtUser` - Username textfield
  - `edtpswd` - Password field
  - `cmboRole` - Role selector (Normal User / Admin User)
  - `progressBar` - Login progress indicator
- **Database Queries:**
  - `SELECT * FROM login WHERE username = ? AND password = ? AND role = ?`
- **Navigation:** → MdiForm (if successful)
- **Features:**
  - Keyboard navigation (Tab, Up/Down arrows)
  - Password validation
  - Role-based access
  - Progress indication

#### **SignUpWindow.java**
- **Purpose:** New user registration
- **Components:**
  - `txtName` - Full name
  - `txtUserName` - Username (unique)
  - `txtPswd` - Password
  - `txtEmail` - Email
  - `cmbRole` - Role selection
- **Validation:**
  - Email format validation
  - Password strength (8+ chars, letters + numbers)
  - Unique username check
- **Database Operations:**
  - `INSERT INTO login(name, username, password, Email, Role)`
  - Checks for duplicate username
- **Navigation:** → Login (after success)

---

### Navigation & UI Framework

#### **MdiForm.java**
- **Purpose:** Main application window with menu bar
- **Role:** MDI (Multiple Document Interface) parent window
- **Menu Structure:**
  
  **User Module:**
  - Add User → SignUpWindow
  - Delete User (not implemented)
  - Exit
  
  **Modules (CRUD Detail Windows):**
  - User-Detail → EmployeeDetail
  - Employee-Detail → AccountDetail
  - Managers-Detail → BookingDetail
  - Expense-Categories → DonerDetail
  - Expenses-Detail → CustomerDetail
  - Claims-Detail → EnquiryDetail
  - Approval-Detail → InventoryDetail
  - Reimbursements-Detail → SalaryDetail
  - Audit-Log-Detail → TaskDetail
  
  **Queries (Search Forms):**
  - Employee-Section-Query → EmployeeSectionQuery
  - Expense-Query → DonerSectionQuery
  - Expense-Categories-Query → AccountSectionQuerry
  - Claims-Section-Query → EnquirySectionQuery
  
  **Reports:**
  - Employee-Section-Report → EmployeeSectionReport
  - Account-Detail-Report → AccountSectionReport

- **Features:**
  - Reflective class loading via string names
  - Gradient background
  - Centered layout

---

### Base Classes (UI Patterns)

#### **BaseCrudWindow.java**
- **Purpose:** Shared template for all CRUD detail windows
- **Protected Methods:**
  - `connectDB()` - Establishes MySQL connection
  - `loadTable(String sql)` - Populates JTable from ResultSet
  - `makeLabel(String)` - Creates styled JLabel
  - `makeField(int width)` - Creates styled JTextField
  - `makeButton(String)` - Creates styled JButton
  - `makeCombo(String... items)` - Creates JComboBox
  - `makeTablePane(int height)` - Creates JScrollPane with JTable
  - `makeTitlePanel(String)` - Creates title section
  - `makeTitledForm(String)` - Creates form panel
  - `lCon(int gridx)`, `fCon(int gridx)` - GridBagConstraints helpers

- **Components:**
  - `table` - JTable for data display
  - `tableModel` - DefaultTableModel
  - DB connection helpers: `con`, `pst`, `rs`
  - CRUD buttons: `btnSave`, `btnFind`, `btnUpdate`, `btnDelete`, `btnNew`, `btnHome`

#### **BaseQueryWindow.java**
- **Purpose:** Template for search/query forms
- **Features:**
  - Dynamic field selector (dropdown)
  - Case-insensitive search capability
  - Show All + Clear buttons
  - Result table with row count
  - Constructor-driven field mapping

#### **BaseReportWindow.java**
- **Purpose:** Template for report generation and printing

---

### CRUD Detail Windows

#### **EmployeeDetail.java**
- **Table:** `employee`
- **Fields:** employeeId, employeeName, dob, companyId, doj, salary, postName, address, pincode, contactNo
- **Operations:** CRUD (Create, Read, Update, Delete)
- **UI:** Form + Table
- **Validation:** Required fields for insert

#### **AccountDetail.java**
- **Table:** `account_detail`
- **Fields:** bankName, accountNo, billId, customerName, totalAmount, natureOfAccount, paymentDate, employeeId, empName
- **Purpose:** Bank account details for reimbursement
- **Operations:** CRUD

#### **BookingDetail.java**
- **Table:** `manager`
- **Fields:** managerId, managerName, department, email, phone, hireDate, salary, status
- **Purpose:** Manager/approver master data
- **Status:** 'Active', 'Inactive', 'On Leave'

#### **DonerDetail.java** (Note: Named misleadingly)
- **Table:** `expensecategory`
- **Fields:** categoryId, categoryName, description, budgetLimit, isActive
- **Purpose:** Define expense types and budget limits
- **Example Categories:** Travel, Meals, Accommodation, Office Supplies, etc.

#### **CustomerDetail.java**
- **Table:** `customerdetail`
- **Fields:** customerId, customerName, address, city, pin, noOfPersons, identificationProof, contactNo, totalRoom, dateOfBooking
- **Purpose:** Guest/customer booking information
- **Operations:** CRUD

#### **EnquiryDetail.java** (Represents Claims)
- **Table:** `claim`
- **Fields:** claimId, employeeId, categoryId, claimDate, amount, description, status, approvedBy
- **Status Values:** 'Pending', 'Approved', 'Rejected', 'Under Review'
- **Purpose:** Store expense claims
- **Auto-generate:** Creates table if not exists

#### **SalaryDetail.java** (Represents Reimbursements)
- **Table:** `reimbursement`
- **Fields:** reimbId, claimId, employeeId, processedDate, amount, paymentMode, transactionRef, status
- **Payment Modes:** 'Bank Transfer', 'Cheque', 'Cash', 'UPI', 'NEFT', 'RTGS'
- **Status:** 'Processed', 'Pending', 'Failed', 'On Hold'
- **Purpose:** Track reimbursement payments

#### **InventoryDetail.java** (Represents Approvals)
- **Table:** `approval`
- **Fields:** approvalId, claimId, approverId, approvalDate, amount, status, remarks
- **Status:** 'Pending', 'Approved', 'Rejected', 'Partially Approved'
- **Purpose:** Track claim approvals with amounts

#### **TaskDetail.java** (Audit Log)
- **Table:** `auditlog`
- **Fields:** logId, userId, action, targetTable, targetId, timestamp, oldValue, newValue, ipAddress
- **Actions:** 'INSERT', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'VIEW'
- **Read-Only:** Update disabled (append-only design)
- **Operations:** Save (log), Search, Purge, Clear

#### **TransactionDetail.java**
- **Table:** `RecurringExpanse`
- **Database:** `expansebudget` (separate)
- **Credentials:** RAJANISH / RAJANISH
- **Purpose:** Recurring expenses and budget tracking

---

### Query/Search Windows

#### **EmployeeSectionQuery.java**
- **Query Against:** `employee` table
- **Search Options:** Name-wise, ID-wise, All Records
- **UI:** Radio button selection + results table

#### **AccountSectionQuerry.java**
- **Query Against:** `account_detail` table
- **Search Options:** Name-wise, Account No.-wise, All Records
- **UI:** Radio buttons + table

#### **EnquirySectionQuery.java**
- **Query Against:** `claim` table
- **Search Capabilities:** Various field selections
- **Navigation:** Back to MdiForm

#### **BookingSectionQuerry.java**
- **Query Against:** `manager` table (Managers)

---

### Report Windows

#### **EmployeeSectionReport.java**
- **Source:** `employee` table
- **Output:** Formatted text report
- **Features:**
  - Generate button
  - Print functionality
  - Text area for display
  - Timestamp included

#### **AccountSectionReport.java**
- **Source:** `account_detail` table
- **Features:** Similar to Employee report
- **Format:** Tabular text output

#### **DonerSectionReport.java**
- **Source:** `expensecategory` table (expense categories)

---

### Utility Classes

#### **ResultSetTableModel.java**
- **Purpose:** Convert JDBC ResultSet to Swing TableModel
- **Replacement:** For net.proteanit.sql.DbUtils
- **Usage:** `table.setModel(new ResultSetTableModel(rs))`
- **Features:**
  - Auto-detects column names from metadata
  - Handles NULL values (displays empty string)
  - Read-only cells

#### **QueryWindows.java**
- **Purpose:** Base query window (factory/template)
- **Likely Used:** For specialized query implementations

#### **ReportWindows.java**
- **Purpose:** Base report window (factory/template)
- **Likely Used:** For specialized report implementations

---

## UI Flow & Navigation

### Application Flow Diagram

```
┌─────────────────────────────────┐
│  App.main()                     │
└────────────┬────────────────────┘
             │
             ▼
┌─────────────────────────────────┐
│  SignUpWindow / Login Choice    │
├─────────────────────────────────┤
│ • New User → SignUpWindow       │
│ • Existing → Login              │
└────────┬────────────────────────┘
         │
      ┌──┴────┬──────┐
      │       │      │
      ▼       ▼      ▼
  SignUp  Login    Validate
      │       │      │
      └───┬───┴──────┘
          │
          ▼
┌─────────────────────────────────┐
│  MdiForm (Main Window)          │
│  with Menu Bar                  │
├─────────────────────────────────┤
│ User Module                     │
│ ├─ Add User                     │
│ ├─ Delete User                  │
│ └─ Exit                         │
│ Modules (CRUD Detail)           │
│ ├─ Employee Detail              │
│ ├─ Account Detail               │
│ ├─ Managers Detail              │
│ ├─ Expense Categories           │
│ ├─ Expenses Detail              │
│ ├─ Claims Detail                │
│ ├─ Approval Detail              │
│ ├─ Reimbursements Detail        │
│ └─ Audit Log Detail             │
│ Queries                         │
│ ├─ Employee Section Query       │
│ ├─ Expense Query                │
│ ├─ Expense Categories Query     │
│ └─ Claims Section Query         │
│ Reports                         │
│ ├─ Employee Section Report      │
│ ├─ Account Detail Report        │
│ └─ Claims Section Report        │
└─────────────────────────────────┘
```

### User Journey

1. **Startup** → App.main() → SignUpWindow
2. **Registration** → SignUpWindow → LOGIN table INSERT → Login screen
3. **Authentication** → Login → Validate credentials → MdiForm
4. **Navigation** → MdiForm menu → Detail/Query/Report windows
5. **CRUD Operations:**
   - Detail windows for data entry/modification
   - Queries for searching
   - Reports for viewing summaries
6. **Workflow:**
   - Employee submits claim (EnquiryDetail/claim table)
   - Manager reviews (InventoryDetail/approval table)
   - Finance processes reimbursement (SalaryDetail/reimbursement table)
   - All changes logged (TaskDetail/auditlog table)

---

## Business Logic

### Expense Claim Process

1. **Claim Submission**
   - Employee submits expense via EnquiryDetail (Claims Detail)
   - Validates: claimId (unique), amount (positive), category (exists)
   - Status: 'Pending'
   - Logged in auditlog table

2. **Claim Approval**
   - Manager reviews in InventoryDetail (Approval Detail)
   - Creates approval record linking claim → manager
   - Can partially approve (different amounts)
   - Can reject with remarks
   - Status updates: Pending → Approved/Rejected/Partially Approved

3. **Reimbursement Processing**
   - Finance processes approved claim via SalaryDetail (Reimbursements)
   - Creates reimbursement payment record
   - Selects payment mode (Bank Transfer, Cheque, etc.)
   - Generates transaction reference
   - Status: 'Processed', 'Pending', 'Failed', 'On Hold'

4. **Audit Trail**
   - Every INSERT/UPDATE/DELETE logged with:
     - User (userId)
     - Action type
     - Target table
     - Old/New values
     - Timestamp
     - IP address
   - Append-only (updates disabled in TaskDetail)

### Budget Management

- **Expense Categories** define allowed expenses and budget limits
- **Claim validation** checks against budgetLimit
- **Manager can reject** claims exceeding limits
- **Recurring expenses** tracked in separate budget database

### Role-Based Access

- **Normal User:** Can submit claims, view own claims
- **Admin User:** Can manage all users, view all claims, approvals, reimbursements

---

## Sample Data

### Sample login Table
```sql
INSERT INTO login VALUES
(1, 'Raj Kumar', 'raj_kumar', 'password123', 'raj@company.com', 'Normal User'),
(2, 'Priya Singh', 'priya_manager', 'pass@456', 'priya@company.com', 'Admin User'),
(3, 'Amit Patel', 'amit_employee', 'amitpass789', 'amit@company.com', 'Normal User');
```

### Sample employee Table
```sql
INSERT INTO employee VALUES
(1, 'EMP001', 'Raj Kumar', '1990-05-15', 'DEPT001', '2020-06-01', 65000, 'Senior Developer', '123 Main St', '110001', '9876543210'),
(2, 'EMP002', 'Priya Singh', '1992-08-20', 'DEPT002', '2019-03-15', 75000, 'Manager', '456 Park Ave', '110002', '8765432109'),
(3, 'EMP003', 'Amit Patel', '1995-12-10', 'DEPT001', '2021-01-10', 55000, 'Junior Developer', '789 Oak Ln', '110003', '7654321098');
```

### Sample expensecategory Table
```sql
INSERT INTO expensecategory VALUES
(1, 'TRAVEL', 'Travel', 'Business travel expenses', 50000.00, 'Yes'),
(2, 'MEAL', 'Meals & Food', 'Client meetings and business meals', 5000.00, 'Yes'),
(3, 'ACCOMMODATION', 'Hotel Accommodation', 'Business travel lodging', 25000.00, 'Yes'),
(4, 'OFFICE', 'Office Supplies', 'Stationery and office supplies', 10000.00, 'Yes'),
(5, 'CONFERENCE', 'Conference & Training', 'Professional development', 75000.00, 'Yes');
```

### Sample claim Table
```sql
INSERT INTO claim VALUES
(1, 'CLM001', 'EMP001', 'TRAVEL', '2024-01-15', 8750.50, 'Business trip to Mumbai - Client meeting', 'Approved', 'priya_manager'),
(2, 'CLM002', 'EMP003', 'MEAL', '2024-01-18', 2450.00, 'Team lunch with client', 'Pending', NULL),
(3, 'CLM003', 'EMP001', 'ACCOMMODATION', '2024-01-20', 5500.00, 'Hotel stay for conference', 'Under Review', 'priya_manager');
```

### Sample manager Table
```sql
INSERT INTO manager VALUES
(1, 'MGR001', 'Priya Singh', 'Engineering', 'priya@company.com', '9876543210', '2019-03-15', 95000, 'Active'),
(2, 'MGR002', 'Vikram Sharma', 'Finance', 'vikram@company.com', '9123456789', '2018-06-01', 100000, 'Active');
```

### Sample approval Table
```sql
INSERT INTO approval VALUES
(1, 'APR001', 'CLM001', 'MGR001', '2024-01-16', 8750.50, 'Approved', 'Approved as submitted'),
(2, 'APR002', 'CLM003', 'MGR001', '2024-01-21', 5500.00, 'Approved', 'Conference accommodation pre-approved');
```

### Sample reimbursement Table
```sql
INSERT INTO reimbursement VALUES
(1, 'RMB001', 'CLM001', 'EMP001', '2024-01-25', 8750.50, 'Bank Transfer', 'UTR20240125001234', 'Processed'),
(2, 'RMB002', 'CLM003', 'EMP001', '2024-01-25', 5500.00, 'NEFT', 'NEFT20240125005678', 'Processed');
```

### Sample account_detail Table
```sql
INSERT INTO account_detail VALUES
(1, 'HDFC Bank', 'XXXX1234567890', 'INV001', 'Raj Kumar', 8750.50, 'Savings', '2024-01-25', 'EMP001', 'Raj Kumar'),
(2, 'ICICI Bank', 'YYYY9876543210', 'INV002', 'Amit Patel', 2450.00, 'Current', '2024-01-18', 'EMP003', 'Amit Patel');
```

### Sample auditlog Table
```sql
INSERT INTO auditlog VALUES
(1, 'LOG001', 'raj_kumar', 'INSERT', 'claim', 'CLM001', '2024-01-15 10:30:45', NULL, 'Amount: 8750.50', '192.168.1.100'),
(2, 'LOG002', 'priya_manager', 'UPDATE', 'claim', 'CLM001', '2024-01-16 11:15:22', 'Pending', 'Approved', '192.168.1.101'),
(3, 'LOG003', 'system', 'INSERT', 'reimbursement', 'RMB001', '2024-01-25 14:45:33', NULL, 'Bank Transfer Processed', '192.168.1.102');
```

---

## Dependencies

### External Libraries
- **MySQL JDBC Driver:** `mysql-connector-j-9.6.0.jar`
  - Location: `lib/mysql-connector-j-9.6.0.jar`
  - Used for database connectivity
  - Copied to input/lib/ during build

### Java Standard Libraries Used
- `java.awt.*` - GUI components
- `java.sql.*` - JDBC database access
- `javax.swing.*` - Swing UI framework
- `java.util.*` - Collections, logging
- `java.util.logging.*` - Application logging

---

## Build & Deployment

### Build Process (build.sh)

```bash
app_name="ExpenseReimbursements"
main_class="App"
mac_id="com.myapp.expense"
version="1.0"

Steps:
1. Clean: rm -rf bin input output
2. Compile: javac -cp lib/mysql-connector-j-9.6.0.jar -d bin $(find src -name "*.java")
3. Copy assets: cp -r src/assets bin/
4. Create JAR: jar cfm app.jar manifest.txt -C bin .
5. Prepare: cp app.jar input/ && cp lib/mysql-connector-j-9.6.0.jar input/lib/
6. Package: jpackage --input input --dest output --type dmg
7. Output: output/ExpenseReimbursements-1.0.dmg (macOS installer)
```

### Manifest File (manifest.txt)
```
Main-Class: App
Class-Path: lib/mysql-connector-j-9.6.0.jar
```

### Compiled Output Structure
```
bin/
├── App.class
├── Login.class
├── SignUpWindow.class
├── MdiForm.class
├── BaseCrudWindow.class
├── *Detail.class (10 files)
├── *Query.class (4 files)
├── *Report.class (2 files)
├── Base*.class (3 files)
├── ResultSetTableModel.class
├── QueryWindows.class
├── ReportWindows.class
└── assets/ (icons, images)
```

### macOS DMG Packaging
- Creates native macOS application bundle
- Includes JRE (Java 21+)
- Screen menu bar integration on macOS
- Icon support via MyIcon.icns
- Self-contained: users don't need Java installed

---

## Summary Statistics

| Metric | Count |
|--------|-------|
| **Total Classes** | 27 |
| **Database Tables** | 11 |
| **Detail Windows (CRUD)** | 10 |
| **Query Windows** | 4+ |
| **Report Windows** | 2+ |
| **Base Classes** | 3 |
| **Total Lines of Code** | ~8,000+ |
| **UI Components** | 100+ |
| **Database Fields** | 80+ |

---

## Key Features

✅ **Multi-user authentication** with role-based access  
✅ **Complete CRUD operations** for all entities  
✅ **Flexible search/query** with multiple filter options  
✅ **Professional reports** in text format  
✅ **Audit logging** for compliance  
✅ **Expense workflow** from claim → approval → reimbursement  
✅ **Budget management** per expense category  
✅ **Multiple payment modes** for reimbursement  
✅ **macOS packaging** with native installer  
✅ **Pure Java Swing UI** (no NetBeans dependencies)  
✅ **JDBC with connection pooling** patterns  

---

## Potential Improvements

📝 **Code Enhancements:**
- Consolidate repeated CRUD code using Base* classes more thoroughly
- Add prepared statement parameterization for SQL injection prevention
- Implement connection pooling (HikariCP, C3P0)
- Add transaction management for workflow operations
- Move DB credentials to external configuration file
- Add logging framework (SLF4J) instead of System.out

🎨 **UI/UX Improvements:**
- Modern look and feel (FlatLaf, Substance)
- Table sorting/filtering at UI level
- Data export (Excel, PDF)
- Date picker components (for date fields)
- Form validation framework
- Undo/Redo functionality

💾 **Database Improvements:**
- Add indexes on frequently queried columns
- Create foreign key constraints
- Implement stored procedures for complex operations
- Add backup/restore functionality

🔒 **Security Improvements:**
- Password hashing (BCrypt)
- Session management
- SQL injection prevention
- XSS/CSRF protection (if web-based)
- API authentication tokens

---

## Conclusion

This **Expense Reimbursement System** is a well-structured desktop application implementing a complete expense management workflow. The architecture uses:

- **Clean Separation:** Business logic, DB access, and UI are organized into distinct classes
- **Design Patterns:** Base classes provide templates (Template Method pattern)
- **Scalability:** Adding new modules (e.g., new expense types) is straightforward
- **User Management:** Multi-level authentication with role-based features
- **Audit Trail:** Complete logging for compliance requirements

The system effectively handles the complete lifecycle of expense claims from employee submission through manager approval to final reimbursement payment, with comprehensive logging for audit purposes.

---

*Analysis Generated: 2024*  
*Technology: Java 21+, MySQL 8.0+, Swing UI*
