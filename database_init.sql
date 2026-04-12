-- ============================================================================
-- Expense Reimbursement System - Database Initialization Script
-- ============================================================================
-- This script creates all tables and populates them with realistic sample data

SET FOREIGN_KEY_CHECKS=0;

-- ============================================================================
-- 1. LOGIN TABLE - User authentication
-- ============================================================================
DROP TABLE IF EXISTS login;
CREATE TABLE login (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Role VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO login(name, username, password, Email, Role) VALUES
('Admin User', 'admin', 'admin123', 'admin@company.com', 'Admin User'),
('John Smith', 'john.smith', 'john123456', 'john.smith@company.com', 'Normal User'),
('Sarah Johnson', 'sarah.johnson', 'sarah123456', 'sarah.johnson@company.com', 'Normal User'),
('Michael Brown', 'michael.brown', 'michael123456', 'michael.brown@company.com', 'Normal User'),
('Emily Davis', 'emily.davis', 'emily123456', 'emily.davis@company.com', 'Normal User'),
('Robert Wilson', 'robert.wilson', 'robert123456', 'robert.wilson@company.com', 'Normal User');

-- ============================================================================
-- 2. EMPLOYEE TABLE - Employee master data
-- ============================================================================
DROP TABLE IF EXISTS employee;
CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employeeId VARCHAR(20) NOT NULL UNIQUE,
    employeename VARCHAR(100) NOT NULL,
    dob VARCHAR(20),
    companyId VARCHAR(20),
    dateofjoin VARCHAR(20),
    Salary DECIMAL(12,2),
    postName VARCHAR(50),
    address VARCHAR(200),
    pincode VARCHAR(10),
    contactno VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO employee(employeeId, employeename, dob, companyId, dateofjoin, Salary, postName, address, pincode, contactno) VALUES
('EMP001', 'John Smith', '1990-05-15', 'COMP001', '2020-01-10', 65000.00, 'Senior Developer', '123 Oak Street, New York, NY', '10001', '555-0101'),
('EMP002', 'Sarah Johnson', '1992-08-22', 'COMP001', '2021-03-15', 72000.00, 'Project Manager', '456 Maple Ave, Los Angeles, CA', '90001', '555-0102'),
('EMP003', 'Michael Brown', '1988-11-30', 'COMP001', '2019-06-20', 68000.00, 'Senior Analyst', '789 Pine Road, Chicago, IL', '60601', '555-0103'),
('EMP004', 'Emily Davis', '1995-02-14', 'COMP002', '2022-07-01', 58000.00, 'Junior Developer', '321 Elm Street, Houston, TX', '77001', '555-0104'),
('EMP005', 'Robert Wilson', '1991-09-08', 'COMP002', '2020-11-05', 64000.00, 'QA Engineer', '654 Cedar Lane, Phoenix, AZ', '85001', '555-0105'),
('EMP006', 'Jennifer Garcia', '1993-03-25', 'COMP001', '2021-02-10', 70000.00, 'Business Analyst', '987 Birch Way, Philadelphia, PA', '19101', '555-0106'),
('EMP007', 'David Martinez', '1989-07-19', 'COMP002', '2020-09-15', 66000.00, 'Systems Admin', '147 Walnut Court, San Antonio, TX', '78201', '555-0107'),
('EMP008', 'Lisa Anderson', '1994-12-03', 'COMP001', '2021-10-01', 60000.00, 'HR Specialist', '258 Spruce Drive, San Diego, CA', '92101', '555-0108');

-- ============================================================================
-- 3. MANAGER TABLE - Approval managers
-- ============================================================================
DROP TABLE IF EXISTS manager;
CREATE TABLE manager (
    id INT AUTO_INCREMENT PRIMARY KEY,
    managerId VARCHAR(20) NOT NULL UNIQUE,
    managerName VARCHAR(100) NOT NULL,
    department VARCHAR(80),
    email VARCHAR(100),
    phone VARCHAR(20),
    hireDate VARCHAR(20),
    salary DECIMAL(12,2),
    status VARCHAR(20) DEFAULT 'Active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO manager(managerId, managerName, department, email, phone, hireDate, salary, status) VALUES
('MGR001', 'Thomas Anderson', 'Engineering', 'thomas.anderson@company.com', '555-0201', '2018-05-10', 95000.00, 'Active'),
('MGR002', 'Patricia Taylor', 'Finance', 'patricia.taylor@company.com', '555-0202', '2017-08-15', 92000.00, 'Active'),
('MGR003', 'James Martinez', 'Operations', 'james.martinez@company.com', '555-0203', '2019-01-20', 88000.00, 'Active'),
('MGR004', 'Maria Garcia', 'Human Resources', 'maria.garcia@company.com', '555-0204', '2018-11-10', 85000.00, 'Active'),
('MGR005', 'Christopher Lee', 'Sales', 'christopher.lee@company.com', '555-0205', '2020-03-05', 90000.00, 'Active');

-- ============================================================================
-- 4. EXPENSE CATEGORY TABLE - Types of expenses
-- ============================================================================
DROP TABLE IF EXISTS expensecategory;
CREATE TABLE expensecategory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoryId VARCHAR(20) NOT NULL UNIQUE,
    categoryName VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    budgetLimit DECIMAL(12,2) DEFAULT 0,
    isActive VARCHAR(10) DEFAULT 'Yes',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO expensecategory(categoryId, categoryName, description, budgetLimit, isActive) VALUES
('CAT001', 'Travel - Accommodation', 'Hotel, lodging expenses during business travel', 50000.00, 'Yes'),
('CAT002', 'Travel - Transportation', 'Flight, train, taxi, parking costs', 40000.00, 'Yes'),
('CAT003', 'Meals & Entertainment', 'Client meals, team lunch, conference dinners', 25000.00, 'Yes'),
('CAT004', 'Office Supplies', 'Stationery, equipment, software licenses', 15000.00, 'Yes'),
('CAT005', 'Training & Development', 'Courses, certifications, workshops', 35000.00, 'Yes'),
('CAT006', 'Conference & Events', 'Registration fees, booth maintenance', 60000.00, 'Yes'),
('CAT007', 'Mobile & Internet', 'Mobile bills, internet, communication tools', 12000.00, 'Yes'),
('CAT008', 'Equipment & Hardware', 'Laptops, monitors, peripherals', 80000.00, 'Yes'),
('CAT009', 'Utilities & Rent', 'Office utilities, meeting room rent', 100000.00, 'Yes');

-- ============================================================================
-- 5. CLAIM TABLE - Expense claims submitted by employees
-- ============================================================================
DROP TABLE IF EXISTS claim;
CREATE TABLE claim (
    id INT AUTO_INCREMENT PRIMARY KEY,
    claimId VARCHAR(20) NOT NULL UNIQUE,
    employeeId VARCHAR(20),
    categoryId VARCHAR(20),
    claimDate VARCHAR(20),
    amount DECIMAL(12,2) DEFAULT 0,
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'Pending',
    approvedBy VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId),
    FOREIGN KEY (categoryId) REFERENCES expensecategory(categoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO claim(claimId, employeeId, categoryId, claimDate, amount, description, status, approvedBy) VALUES
('CLM001', 'EMP001', 'CAT001', '2024-01-05', 2500.00, 'Hotel stay for New York client meeting', 'Approved', 'MGR001'),
('CLM002', 'EMP002', 'CAT002', '2024-01-10', 850.00, 'Flight tickets to Chicago conference', 'Approved', 'MGR001'),
('CLM003', 'EMP003', 'CAT003', '2024-01-12', 425.00, 'Client lunch meeting - 4 people', 'Approved', 'MGR002'),
('CLM004', 'EMP004', 'CAT004', '2024-01-15', 320.00, 'Office supplies and software', 'Pending', NULL),
('CLM005', 'EMP005', 'CAT005', '2024-01-18', 1200.00, 'AWS certification course', 'Under Review', 'MGR001'),
('CLM006', 'EMP006', 'CAT006', '2024-01-20', 5000.00, 'TechConf 2024 registration', 'Approved', 'MGR001'),
('CLM007', 'EMP001', 'CAT002', '2024-02-01', 650.00, 'Taxi and parking expenses - Boston trip', 'Approved', 'MGR001'),
('CLM008', 'EMP007', 'CAT001', '2024-02-05', 1800.00, 'Hotel for 3 days - San Francisco', 'Pending', NULL),
('CLM009', 'EMP008', 'CAT003', '2024-02-08', 380.00, 'Team lunch celebration', 'Approved', 'MGR003'),
('CLM010', 'EMP002', 'CAT005', '2024-02-10', 950.00, 'Project Management certification', 'Under Review', 'MGR001'),
('CLM011', 'EMP003', 'CAT002', '2024-02-12', 1200.00, 'Round trip flight - Denver', 'Approved', 'MGR001'),
('CLM012', 'EMP004', 'CAT001', '2024-02-15', 2200.00, 'Hotel and miscellaneous - Seattle trip', 'Pending', NULL);

-- ============================================================================
-- 6. APPROVAL TABLE - Manager approvals for claims
-- ============================================================================
DROP TABLE IF EXISTS approval;
CREATE TABLE approval (
    id INT AUTO_INCREMENT PRIMARY KEY,
    approvalId VARCHAR(20) NOT NULL UNIQUE,
    claimId VARCHAR(20),
    approverId VARCHAR(20),
    approvalDate VARCHAR(20),
    amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'Pending',
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claimId) REFERENCES claim(claimId),
    FOREIGN KEY (approverId) REFERENCES manager(managerId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO approval(approvalId, claimId, approverId, approvalDate, amount, status, remarks) VALUES
('APR001', 'CLM001', 'MGR001', '2024-01-06', 2500.00, 'Approved', 'Approved as per policy'),
('APR002', 'CLM002', 'MGR001', '2024-01-11', 850.00, 'Approved', 'Conference attendance approved'),
('APR003', 'CLM003', 'MGR002', '2024-01-13', 425.00, 'Approved', 'Client entertainment approved'),
('APR004', 'CLM005', 'MGR001', '2024-01-19', 1200.00, 'Under Review', 'Awaiting budget confirmation'),
('APR005', 'CLM006', 'MGR001', '2024-01-21', 5000.00, 'Approved', 'Conference registration approved'),
('APR006', 'CLM007', 'MGR001', '2024-02-02', 650.00, 'Approved', 'Travel expenses approved'),
('APR007', 'CLM009', 'MGR003', '2024-02-09', 380.00, 'Approved', 'Team event approved'),
('APR008', 'CLM010', 'MGR001', '2024-02-11', 950.00, 'Under Review', 'Pending department approval'),
('APR009', 'CLM011', 'MGR001', '2024-02-13', 1200.00, 'Approved', 'Travel approved'),
('APR010', 'CLM004', 'MGR002', '2024-01-16', 320.00, 'Pending', 'Review needed');

-- ============================================================================
-- 7. REIMBURSEMENT TABLE - Payment processing
-- ============================================================================
DROP TABLE IF EXISTS reimbursement;
CREATE TABLE reimbursement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reimbId VARCHAR(20) NOT NULL UNIQUE,
    claimId VARCHAR(20),
    employeeId VARCHAR(20),
    processedDate VARCHAR(20),
    amount DECIMAL(12,2) DEFAULT 0,
    paymentMode VARCHAR(30) DEFAULT 'Bank Transfer',
    transactionRef VARCHAR(50),
    status VARCHAR(20) DEFAULT 'Processed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claimId) REFERENCES claim(claimId),
    FOREIGN KEY (employeeId) REFERENCES employee(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO reimbursement(reimbId, claimId, employeeId, processedDate, amount, paymentMode, transactionRef, status) VALUES
('REIMB001', 'CLM001', 'EMP001', '2024-01-08', 2500.00, 'Bank Transfer', 'TXN20240108001', 'Processed'),
('REIMB002', 'CLM002', 'EMP002', '2024-01-15', 850.00, 'Bank Transfer', 'TXN20240115001', 'Processed'),
('REIMB003', 'CLM003', 'EMP003', '2024-01-18', 425.00, 'Cheque', 'CHQ20240118001', 'Processed'),
('REIMB004', 'CLM006', 'EMP006', '2024-01-25', 5000.00, 'Bank Transfer', 'TXN20240125001', 'Processed'),
('REIMB005', 'CLM007', 'EMP001', '2024-02-05', 650.00, 'UPI', 'UPI20240205001', 'Processed'),
('REIMB006', 'CLM009', 'EMP008', '2024-02-12', 380.00, 'Bank Transfer', 'TXN20240212001', 'Processed'),
('REIMB007', 'CLM011', 'EMP003', '2024-02-18', 1200.00, 'NEFT', 'NEFT20240218001', 'Processed');

-- ============================================================================
-- 8. ACCOUNT DETAIL TABLE - Employee bank accounts
-- ============================================================================
DROP TABLE IF EXISTS account_detail;
CREATE TABLE account_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bankname VARCHAR(100),
    accountno VARCHAR(20) NOT NULL UNIQUE,
    employeeid VARCHAR(20),
    employeename VARCHAR(100),
    grosssal DECIMAL(12,2),
    natureofacount VARCHAR(50),
    dateofopen VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employeeid) REFERENCES employee(employeeId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO account_detail(bankname, accountno, employeeid, employeename, grosssal, natureofacount, dateofopen) VALUES
('State Bank of India', '12345678901234', 'EMP001', 'John Smith', 65000.00, 'Savings', '2020-01-15'),
('HDFC Bank', '98765432109876', 'EMP002', 'Sarah Johnson', 72000.00, 'Current', '2021-03-20'),
('ICICI Bank', '55667788990011', 'EMP003', 'Michael Brown', 68000.00, 'Savings', '2019-06-25'),
('Axis Bank', '11223344556677', 'EMP004', 'Emily Davis', 58000.00, 'Savings', '2022-07-10'),
('State Bank of India', '44556677889900', 'EMP005', 'Robert Wilson', 64000.00, 'Current', '2020-11-12'),
('HDFC Bank', '77889900112233', 'EMP006', 'Jennifer Garcia', 70000.00, 'Savings', '2021-02-18'),
('ICICI Bank', '33445566778899', 'EMP007', 'David Martinez', 66000.00, 'Savings', '2020-09-22'),
('Axis Bank', '66778899001122', 'EMP008', 'Lisa Anderson', 60000.00, 'Current', '2021-10-08');

-- ============================================================================
-- 9. AUDIT LOG TABLE - System audit trail (append-only)
-- ============================================================================
DROP TABLE IF EXISTS auditlog;
CREATE TABLE auditlog (
    id INT AUTO_INCREMENT PRIMARY KEY,
    logId VARCHAR(30) NOT NULL UNIQUE,
    userId VARCHAR(30),
    action VARCHAR(20),
    targetTable VARCHAR(50),
    targetId VARCHAR(30),
    timestamp VARCHAR(30),
    oldValue VARCHAR(255),
    newValue VARCHAR(255),
    ipAddress VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (userId),
    INDEX idx_action (action),
    INDEX idx_table (targetTable)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO auditlog(logId, userId, action, targetTable, targetId, timestamp, oldValue, newValue, ipAddress) VALUES
('LOG001', 'john.smith', 'INSERT', 'claim', 'CLM001', '2024-01-05 09:30:00', 'NULL', 'CLM001,EMP001,CAT001,2500.00', '192.168.1.101'),
('LOG002', 'admin', 'UPDATE', 'claim', 'CLM001', '2024-01-06 11:15:00', 'status=Pending', 'status=Approved', '192.168.1.100'),
('LOG003', 'sarah.johnson', 'INSERT', 'claim', 'CLM002', '2024-01-10 10:45:00', 'NULL', 'CLM002,EMP002,CAT002,850.00', '192.168.1.102'),
('LOG004', 'michael.brown', 'INSERT', 'approval', 'APR001', '2024-01-06 11:20:00', 'NULL', 'APR001,CLM001,MGR001,Approved', '192.168.1.100'),
('LOG005', 'admin', 'INSERT', 'reimbursement', 'REIMB001', '2024-01-08 14:30:00', 'NULL', 'REIMB001,CLM001,EMP001,2500.00', '192.168.1.100'),
('LOG006', 'emily.davis', 'INSERT', 'claim', 'CLM004', '2024-01-15 08:20:00', 'NULL', 'CLM004,EMP004,CAT004,320.00', '192.168.1.104'),
('LOG007', 'robert.wilson', 'INSERT', 'claim', 'CLM005', '2024-01-18 13:45:00', 'NULL', 'CLM005,EMP005,CAT005,1200.00', '192.168.1.105'),
('LOG008', 'admin', 'UPDATE', 'claim', 'CLM005', '2024-01-19 09:00:00', 'status=Pending', 'status=Under Review', '192.168.1.100'),
('LOG009', 'john.smith', 'DELETE', 'claim', 'CLM012', '2024-02-20 15:30:00', 'CLM012,EMP004,CAT001,2200.00', 'NULL', '192.168.1.101'),
('LOG010', 'admin', 'INSERT', 'employee', 'EMP009', '2024-02-01 10:00:00', 'NULL', 'EMP009,NewEmployee,1995-05-12...', '192.168.1.100');

-- ============================================================================
-- 10. CUSTOMER DETAIL TABLE - Guest/Customer information
-- ============================================================================
DROP TABLE IF EXISTS customerdetail;
CREATE TABLE customerdetail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cusid VARCHAR(20) NOT NULL UNIQUE,
    custname VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    pincode VARCHAR(10),
    noofperson INT,
    identityproof VARCHAR(50),
    contno VARCHAR(20),
    noofroom INT,
    dateofbooking VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO customerdetail(cusid, custname, address, city, pincode, noofperson, identityproof, contno, noofroom, dateofbooking) VALUES
('CUST001', 'Robert Johnson', '123 Business Park, Downtown', 'New York', '10001', 2, 'Passport', '555-1001', 2, '2024-01-10'),
('CUST002', 'Jennifer Smith', '456 Corporate Plaza, North', 'Los Angeles', '90001', 1, 'Driver License', '555-1002', 1, '2024-01-15'),
('CUST003', 'William Brown', '789 Commerce Street, East', 'Chicago', '60601', 3, 'Passport', '555-1003', 2, '2024-01-18'),
('CUST004', 'Mary Davis', '321 Enterprise Blvd, West', 'Houston', '77001', 1, 'National ID', '555-1004', 1, '2024-01-22'),
('CUST005', 'James Wilson', '654 Industry Road, South', 'Phoenix', '85001', 2, 'Passport', '555-1005', 2, '2024-02-01'),
('CUST006', 'Patricia Garcia', '987 Trade Center, Central', 'Philadelphia', '19101', 4, 'Driver License', '555-1006', 3, '2024-02-05'),
('CUST007', 'Richard Martinez', '147 Business District, North', 'San Antonio', '78201', 1, 'Passport', '555-1007', 1, '2024-02-08'),
('CUST008', 'Sharon Anderson', '258 Trade Hub, South', 'San Diego', '92101', 2, 'National ID', '555-1008', 2, '2024-02-12');

-- ============================================================================
-- 11. RECURRING EXPANSE TABLE - Budget tracking
-- ============================================================================
DROP TABLE IF EXISTS RecurringExpanse;
CREATE TABLE RecurringExpanse (
    id INT AUTO_INCREMENT PRIMARY KEY,
    RecurringExpanseID VARCHAR(20) NOT NULL UNIQUE,
    FirstName VARCHAR(100),
    LastName VARCHAR(100),
    EmployeeID VARCHAR(20),
    Year INT,
    Month VARCHAR(20),
    ExpenseAmount DECIMAL(12,2),
    Category VARCHAR(50),
    Description VARCHAR(255),
    Status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO RecurringExpanse(RecurringExpanseID, FirstName, LastName, EmployeeID, Year, Month, ExpenseAmount, Category, Description, Status) VALUES
('REC001', 'John', 'Smith', 'EMP001', 2024, 'January', 5200.00, 'Travel', 'Monthly travel allowance', 'Approved'),
('REC002', 'Sarah', 'Johnson', 'EMP002', 2024, 'January', 4500.00, 'Office', 'Office supplies and materials', 'Approved'),
('REC003', 'Michael', 'Brown', 'EMP003', 2024, 'January', 3200.00, 'Training', 'Training programs', 'Pending'),
('REC004', 'Emily', 'Davis', 'EMP004', 2024, 'January', 2800.00, 'Equipment', 'Equipment maintenance', 'Approved'),
('REC005', 'Robert', 'Wilson', 'EMP005', 2024, 'January', 3500.00, 'Travel', 'Monthly travel allowance', 'Approved'),
('REC006', 'Jennifer', 'Garcia', 'EMP006', 2024, 'February', 5200.00, 'Travel', 'Monthly travel allowance', 'Approved'),
('REC007', 'David', 'Martinez', 'EMP007', 2024, 'February', 4000.00, 'Office', 'Office supplies and materials', 'Approved'),
('REC008', 'Lisa', 'Anderson', 'EMP008', 2024, 'February', 2900.00, 'Equipment', 'Equipment maintenance', 'Pending');

SET FOREIGN_KEY_CHECKS=1;

-- ============================================================================
-- Database setup complete
-- ============================================================================
COMMIT;
