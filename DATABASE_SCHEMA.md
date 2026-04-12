# Database Schema - Visual Reference Guide

## Database: expanses

### Entity-Relationship Diagram (Logical Structure)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         EXPENSE REIMBURSEMENT SYSTEM                         │
│                           DATABASE SCHEMA (expanses)                         │
└─────────────────────────────────────────────────────────────────────────────┘


┌──────────────────┐
│     login        │ (Authentication)
├──────────────────┤
│ id (PK)          │
│ name             │
│ username (UQ)    │
│ password         │
│ Email            │
│ Role             │
└──────────────────┘
       ▲
       │ (login.username → auditlog.userId)
       │
       │
┌─────────────────────────────────────────────────────────────────────┐
│                                                                     │
│  ┌──────────────────────┐         ┌──────────────────────────┐    │
│  │    employee (PK)     │◄────────┤  account_detail (FK)     │    │
│  ├──────────────────────┤         ├──────────────────────────┤    │
│  │ id                   │         │ id                       │    │
│  │ employeeId (UQ)      │         │ bankName                 │    │
│  │ employeeName         │         │ accountNo                │    │
│  │ dob                  │         │ billId                   │    │
│  │ companyId            │         │ customerName             │    │
│  │ doj                  │         │ totalAmount              │    │
│  │ salary               │         │ natureOfAccount          │    │
│  │ postName             │         │ paymentDate              │    │
│  │ address              │         │ employeeId (FK)          │    │
│  │ pincode              │         │ empName                  │    │
│  │ contactNo            │         └──────────────────────────┘    │
│  └──────────────────────┘                                         │
│           ▲                                                        │
│           │ (claim.employeeId, reimbursement.employeeId)         │
│           │                                                        │
│  ┌────────┴────────────────────────────────────────────────┐      │
│  │                                                         │      │
│  ┌──────────────────┐      ┌──────────────────────┐      │      │
│  │   manager (PK)   │◄─────┤  approval (FK to)    │      │      │
│  ├──────────────────┤      ├──────────────────────┤      │      │
│  │ id               │      │ id                   │      │      │
│  │ managerId (UQ)   │      │ approvalId (UQ)      │      │      │
│  │ managerName      │      │ claimId (FK) ────────┼──┐   │      │
│  │ department       │      │ approverId (FK)      │  │   │      │
│  │ email            │      │ approvalDate         │  │   │      │
│  │ phone            │      │ amount               │  │   │      │
│  │ hireDate         │      │ status               │  │   │      │
│  │ salary           │      │ remarks              │  │   │      │
│  │ status           │      └──────────────────────┘  │   │      │
│  └──────────────────┘                               │   │      │
│                                                     │   │      │
│  ┌──────────────────────────┐   ┌──────────────────┴─┐ │      │
│  │   expensecategory (PK)   │   │   claim (FK to)  │ │      │
│  ├──────────────────────────┤   ├──────────────────┤ │      │
│  │ id                       │   │ id               │ │      │
│  │ categoryId (UQ)          │   │ claimId (UQ)     │ │      │
│  │ categoryName             │   │ employeeId (FK)  │ │      │
│  │ description              │◄──┤ categoryId (FK)  │ │      │
│  │ budgetLimit              │   │ claimDate        │ │      │
│  │ isActive                 │   │ amount           │ │      │
│  └──────────────────────────┘   │ description      │ │      │
│                                 │ status           │ │      │
│                                 │ approvedBy       │ │      │
│                                 └──────────────────┘ │      │
│                                        ▲             │      │
│                                        └─────────────┘      │
│                                                             │
│  ┌──────────────────────────┐  ┌──────────────────────┐   │
│  │  reimbursement (FK to)   │  │   auditlog (log)     │   │
│  ├──────────────────────────┤  ├──────────────────────┤   │
│  │ id                       │  │ id                   │   │
│  │ reimbId (UQ)             │  │ logId (UQ)           │   │
│  │ claimId (FK) ────────────┼─►│ userId (FK)          │   │
│  │ employeeId (FK)          │  │ action               │   │
│  │ processedDate            │  │ targetTable          │   │
│  │ amount                   │  │ targetId             │   │
│  │ paymentMode              │  │ timestamp            │   │
│  │ transactionRef           │  │ oldValue             │   │
│  │ status                   │  │ newValue             │   │
│  └──────────────────────────┘  │ ipAddress            │   │
│                                 └──────────────────────┘   │
│                                                             │
│  ┌──────────────────────────┐                             │
│  │  customerdetail (guest)  │                             │
│  ├──────────────────────────┤                             │
│  │ id                       │                             │
│  │ customerId               │                             │
│  │ customerName             │                             │
│  │ address                  │                             │
│  │ city                     │                             │
│  │ pin                      │                             │
│  │ noOfPersons              │                             │
│  │ identificationProof      │                             │
│  │ contactNo                │                             │
│  │ totalRoom                │                             │
│  │ dateOfBooking            │                             │
│  └──────────────────────────┘                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Table Structure Details

### 1. login
```
+--------+--------------+------+-----+---------+----------------+
| Field  | Type         | Null | Key | Default | Extra          |
+--------+--------------+------+-----+---------+----------------+
| id     | INT          | NO   | PRI | NULL    | AUTO_INCREMENT |
| name   | VARCHAR(100) | NO   |     | NULL    |                |
| usrnm  | VARCHAR(50)  | NO   | UNI | NULL    |                |
| pswd   | VARCHAR(100) | NO   |     | NULL    |                |
| Email  | VARCHAR(100) | YES  |     | NULL    |                |
| Role   | VARCHAR(50)  | YES  |     | NULL    |                |
+--------+--------------+------+-----+---------+----------------+
```

### 2. employee
```
+----------+---------------+------+-----+---------+----------------+
| Field    | Type          | Null | Key | Default | Extra          |
+----------+---------------+------+-----+---------+----------------+
| id       | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| empId    | VARCHAR(20)   | NO   | UNI | NULL    |                |
| empName  | VARCHAR(100)  | NO   |     | NULL    |                |
| dob      | VARCHAR(20)   | YES  |     | NULL    |                |
| compId   | VARCHAR(20)   | YES  |     | NULL    |                |
| doj      | VARCHAR(20)   | YES  |     | NULL    |                |
| salary   | DECIMAL(12,2) | YES  |     | NULL    |                |
| postName | VARCHAR(50)   | YES  |     | NULL    |                |
| address  | VARCHAR(200)  | YES  |     | NULL    |                |
| pincode  | VARCHAR(10)   | YES  |     | NULL    |                |
| contactNo| VARCHAR(20)   | YES  |     | NULL    |                |
+----------+---------------+------+-----+---------+----------------+
```

### 3. account_detail
```
+-----------------+---------------+------+-----+---------+----------------+
| Field           | Type          | Null | Key | Default | Extra          |
+-----------------+---------------+------+-----+---------+----------------+
| id              | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| bankName        | VARCHAR(100)  | YES  |     | NULL    |                |
| accountNo       | VARCHAR(30)   | YES  |     | NULL    |                |
| billId          | VARCHAR(30)   | YES  |     | NULL    |                |
| customerName    | VARCHAR(100)  | YES  |     | NULL    |                |
| totalAmount     | DECIMAL(12,2) | YES  |     | NULL    |                |
| natureOfAccount | VARCHAR(50)   | YES  |     | NULL    |                |
| paymentDate     | VARCHAR(20)   | YES  |     | NULL    |                |
| employeeId      | VARCHAR(20)   | YES  |     | NULL    |                |
| empName         | VARCHAR(100)  | YES  |     | NULL    |                |
+-----------------+---------------+------+-----+---------+----------------+
```

### 4. manager
```
+-----------+---------------+------+-----+---------+----------------+
| Field     | Type          | Null | Key | Default | Extra          |
+-----------+---------------+------+-----+---------+----------------+
| id        | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| mgId      | VARCHAR(20)   | NO   | UNI | NULL    |                |
| mgName    | VARCHAR(100)  | NO   |     | NULL    |                |
| dept      | VARCHAR(80)   | YES  |     | NULL    |                |
| email     | VARCHAR(100)  | YES  |     | NULL    |                |
| phone     | VARCHAR(20)   | YES  |     | NULL    |                |
| hireDate  | VARCHAR(20)   | YES  |     | NULL    |                |
| salary    | DECIMAL(12,2) | YES  |     | NULL    |                |
| status    | VARCHAR(20)   | YES  |     | Active  |                |
+-----------+---------------+------+-----+---------+----------------+
```

### 5. expensecategory
```
+------------+---------------+------+-----+---------+----------------+
| Field      | Type          | Null | Key | Default | Extra          |
+------------+---------------+------+-----+---------+----------------+
| id         | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| catId      | VARCHAR(20)   | NO   | UNI | NULL    |                |
| catName    | VARCHAR(100)  | NO   |     | NULL    |                |
| desc       | VARCHAR(255)  | YES  |     | NULL    |                |
| budgetLim  | DECIMAL(12,2) | YES  |     | 0       |                |
| isActive   | VARCHAR(10)   | YES  |     | Yes     |                |
+------------+---------------+------+-----+---------+----------------+
```

### 6. claim
```
+----------+---------------+------+-----+---------+----------------+
| Field    | Type          | Null | Key | Default | Extra          |
+----------+---------------+------+-----+---------+----------------+
| id       | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| claimId  | VARCHAR(20)   | NO   | UNI | NULL    |                |
| empId    | VARCHAR(20)   | YES  |     | NULL    |                |
| catId    | VARCHAR(20)   | YES  |     | NULL    |                |
| claimDt  | VARCHAR(20)   | YES  |     | NULL    |                |
| amount   | DECIMAL(12,2) | YES  |     | 0       |                |
| desc     | VARCHAR(255)  | YES  |     | NULL    |                |
| status   | VARCHAR(20)   | YES  |     | Pending |                |
| approver | VARCHAR(50)   | YES  |     | NULL    |                |
+----------+---------------+------+-----+---------+----------------+
```

### 7. approval
```
+----------+---------------+------+-----+---------+----------------+
| Field    | Type          | Null | Key | Default | Extra          |
+----------+---------------+------+-----+---------+----------------+
| id       | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| apprId   | VARCHAR(20)   | NO   | UNI | NULL    |                |
| claimId  | VARCHAR(20)   | YES  |     | NULL    |                |
| approvId | VARCHAR(20)   | YES  |     | NULL    |                |
| apprDate | VARCHAR(20)   | YES  |     | NULL    |                |
| amount   | DECIMAL(12,2) | YES  |     | 0       |                |
| status   | VARCHAR(20)   | YES  |     | Pending |                |
| remarks  | VARCHAR(255)  | YES  |     | NULL    |                |
+----------+---------------+------+-----+---------+----------------+
```

### 8. reimbursement
```
+----------+---------------+------+-----+---------+----------------+
| Field    | Type          | Null | Key | Default | Extra          |
+----------+---------------+------+-----+---------+----------------+
| id       | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| reimbId  | VARCHAR(20)   | NO   | UNI | NULL    |                |
| claimId  | VARCHAR(20)   | YES  |     | NULL    |                |
| empId    | VARCHAR(20)   | YES  |     | NULL    |                |
| procDt   | VARCHAR(20)   | YES  |     | NULL    |                |
| amount   | DECIMAL(12,2) | YES  |     | 0       |                |
| payMode  | VARCHAR(30)   | YES  |     | Bank Tr |                |
| txnRef   | VARCHAR(50)   | YES  |     | NULL    |                |
| status   | VARCHAR(20)   | YES  |     | Process |                |
+----------+---------------+------+-----+---------+----------------+
```

### 9. auditlog
```
+----------+---------------+------+-----+---------+----------------+
| Field    | Type          | Null | Key | Default | Extra          |
+----------+---------------+------+-----+---------+----------------+
| id       | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| logId    | VARCHAR(30)   | NO   | UNI | NULL    |                |
| userId   | VARCHAR(30)   | YES  |     | NULL    |                |
| action   | VARCHAR(20)   | YES  |     | NULL    |                |
| tgtTbl   | VARCHAR(50)   | YES  |     | NULL    |                |
| tgtId    | VARCHAR(30)   | YES  |     | NULL    |                |
| ts       | VARCHAR(30)   | YES  |     | NULL    |                |
| oldVal   | VARCHAR(255)  | YES  |     | NULL    |                |
| newVal   | VARCHAR(255)  | YES  |     | NULL    |                |
| ipAddr   | VARCHAR(30)   | YES  |     | NULL    |                |
+----------+---------------+------+-----+---------+----------------+
```

### 10. customerdetail
```
+-----------+---------------+------+-----+---------+----------------+
| Field     | Type          | Null | Key | Default | Extra          |
+-----------+---------------+------+-----+---------+----------------+
| id        | INT           | NO   | PRI | NULL    | AUTO_INCREMENT |
| custId    | VARCHAR(20)   | YES  |     | NULL    |                |
| custName  | VARCHAR(100)  | YES  |     | NULL    |                |
| address   | VARCHAR(200)  | YES  |     | NULL    |                |
| city      | VARCHAR(50)   | YES  |     | NULL    |                |
| pin       | VARCHAR(10)   | YES  |     | NULL    |                |
| noPerson  | VARCHAR(10)   | YES  |     | NULL    |                |
| identProf | VARCHAR(50)   | YES  |     | NULL    |                |
| contactNo | VARCHAR(20)   | YES  |     | NULL    |                |
| totalRoom | VARCHAR(10)   | YES  |     | NULL    |                |
| bookDt    | VARCHAR(20)   | YES  |     | NULL    |                |
+-----------+---------------+------+-----+---------+----------------+
```

---

## Key Constraints & Indices

### Unique Constraints (UNI)
- `login.username` - User login must be unique
- `employee.employeeId` - Employee ID must be unique
- `manager.managerId` - Manager ID must be unique
- `expensecategory.categoryId` - Category ID must be unique
- `claim.claimId` - Claim ID must be unique
- `approval.approvalId` - Approval ID must be unique
- `reimbursement.reimbId` - Reimbursement ID must be unique
- `auditlog.logId` - Audit log ID must be unique

### Primary Keys (PRI)
- All tables use `id` as auto-incrementing primary key

### Foreign Keys (Relationships)
```
claim.employeeId      → employee.employeeId
claim.categoryId      → expensecategory.categoryId
approval.claimId      → claim.claimId
approval.approverId   → manager.managerId
reimbursement.claimId → claim.claimId
reimbursement.empId   → employee.employeeId
account_detail.empId  → employee.employeeId
auditlog.userId       → login.username
```

---

## Data Type Conventions

| Type | Usage | Examples |
|------|-------|----------|
| **VARCHAR(20)** | ID codes | EMP001, CLM001, MGR001, APR001, RMB001, CAT001 |
| **VARCHAR(50-100)** | Names, descriptions | Employee names, manager names, email |
| **VARCHAR(255)** | Long text | Descriptions, remarks, audit details |
| **DECIMAL(12,2)** | Money | Salary, amount, budget limits |
| **VARCHAR(20)** | Dates | Stored as strings (format: YYYY-MM-DD or locale-specific) |
| **INT AUTO_INCREMENT** | Primary keys | System-generated unique ID for each row |
| **VARCHAR(30)** | Status fields | 'Active', 'Pending', 'Approved', 'Rejected' |

---

## Normalization Analysis

### (Partial 2NF)
- ✅ All attributes depend on primary key
- ✅ No partial dependencies for candidate keys
- ⚠️ Some denormalization present (e.g., `account_detail.empName` duplicates `employee.employeeName`)
- ⚠️ Audit log stores both old and new values (by design for compliance)

### Recommendations
- Add explicit `NOT NULL` constraints where applicable
- Add foreign key constraints in DDL
- Create indices on frequently queried columns:
  - `claim(employeeId)`, `claim(categoryId)`, `claim(status)`
  - `approval(claimId)`, `approval(approverId)`
  - `reimbursement(employeeId)`, `reimbursement(status)`
  - `auditlog(userId)`, `auditlog(targetTable)`, `auditlog(timestamp)`

---

## SQL Examples

### Create Database
```sql
CREATE DATABASE IF NOT EXISTS expanses 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE expanses;
```

### Create Tables (DDL)
```sql
CREATE TABLE login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Role VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ... (similar for other tables)
```

### Common Queries

#### Get all pending claims for a manager
```sql
SELECT c.claimId, c.employeeId, e.employeeName, c.amount, c.description, c.claimDate
FROM claim c
JOIN employee e ON c.employeeId = e.employeeId
WHERE c.status = 'Pending'
ORDER BY c.claimDate DESC;
```

#### Get reimbursement status for an employee
```sql
SELECT r.reimbId, r.claimId, r.amount, r.paymentMode, r.transactionRef, r.status
FROM reimbursement r
WHERE r.employeeId = 'EMP001'
ORDER BY r.processedDate DESC;
```

#### Get audit trail for a specific record
```sql
SELECT logId, userId, action, oldValue, newValue, timestamp
FROM auditlog
WHERE targetTable = 'claim' AND targetId = 'CLM001'
ORDER BY timestamp DESC;
```

#### Get budget utilization by category
```sql
SELECT 
    ec.categoryId,
    ec.categoryName,
    ec.budgetLimit,
    SUM(c.amount) as totalClaimed,
    (ec.budgetLimit - SUM(c.amount)) as remainingBudget
FROM expensecategory ec
LEFT JOIN claim c ON ec.categoryId = c.categoryId AND c.status IN ('Approved', 'Under Review')
GROUP BY ec.categoryId, ec.categoryName
ORDER BY remainingBudget ASC;
```

---

## Connection Settings

### Application Configuration
```properties
# In Java source files (hardcoded - should be externalized)
DB_URL = "jdbc:mysql://localhost:3306/expanses?useSSL=false"
DB_USER = "root"
DB_PASS = "tiger"

# Optional: Budget database (separate)
DB_URL_BUDGET = "jdbc:mysql://localhost:3306/expansebudget?useSSL=false"
DB_USER_BUDGET = "RAJANISH"
DB_PASS_BUDGET = "RAJANISH"
```

### Recommended External Configuration (config.properties)
```properties
# MySQL Connection
mysql.host=localhost
mysql.port=3306
mysql.database=expanses
mysql.username=root
mysql.password=${MYSQL_PASSWORD}
mysql.useSSL=false

# Application
app.name=Expense Reimbursement System
app.version=1.0
app.log.level=INFO
```

---

## Performance Considerations

### Current Issues
- No connection pooling (new connection per operation)
- No query optimization
- No indices defined
- String-based date storage (harder to query)

### Optimization Recommendations
```sql
-- Add indices
CREATE INDEX idx_claim_empid ON claim(employeeId);
CREATE INDEX idx_claim_status ON claim(status);
CREATE INDEX idx_approval_claimid ON approval(claimId);
CREATE INDEX idx_reimbursement_empid ON reimbursement(employeeId);
CREATE INDEX idx_reimbursement_status ON reimbursement(status);
CREATE INDEX idx_auditlog_userid ON auditlog(userId);
CREATE INDEX idx_auditlog_timestamp ON auditlog(timestamp DESC);

-- Convert dates to DATE type
ALTER TABLE employee MODIFY COLUMN dob DATE;
ALTER TABLE employee MODIFY COLUMN doj DATE;
ALTER TABLE claim MODIFY COLUMN claimDate DATE;
ALTER TABLE approval MODIFY COLUMN approvalDate DATE;
ALTER TABLE reimbursement MODIFY COLUMN processedDate DATE;
ALTER TABLE auditlog MODIFY COLUMN timestamp DATETIME;
```

---

*Database Schema Reference - Expense Reimbursement System v1.0*
