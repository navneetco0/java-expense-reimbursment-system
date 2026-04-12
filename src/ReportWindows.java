// import javax.swing.SwingUtilities;

// ════════════════════════════════════════════════════════════
//  EmployeeSectionReport – Employee Section Report
// ════════════════════════════════════════════════════════════
// class EmployeeSectionReport extends BaseReportWindow {
//     public EmployeeSectionReport() {
//         super("EMPLOYEE SECTION REPORT", "employee", "Salary",
//             new String[][]{
//                 {"Employee ID",   "employeeId"},
//                 {"Name",          "employeename"},
//                 {"Department ID", "companyId"},
//                 {"Post/Role",     "postName"},
//                 {"Contact No",    "contactno"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new EmployeeSectionReport().setVisible(true));
//     }
// }

// ════════════════════════════════════════════════════════════
//  BookingSectionReport – Managers Section Report
// ════════════════════════════════════════════════════════════
// class BookingSectionReport extends BaseReportWindow {
//     public BookingSectionReport() {
//         super("MANAGERS SECTION REPORT", "manager", "salary",
//             new String[][]{
//                 {"Manager ID",   "managerId"},
//                 {"Manager Name", "managerName"},
//                 {"Department",   "department"},
//                 {"Status",       "status"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new BookingSectionReport().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  ExpenseSectionReport – Expense Section Report
// // ════════════════════════════════════════════════════════════
// class ExpenseSectionReport extends BaseReportWindow {
//     public ExpenseSectionReport() {
//         super("EXPENSE SECTION REPORT", "expensecategory", "budgetLimit",
//             new String[][]{
//                 {"Category ID",   "categoryId"},
//                 {"Category Name", "categoryName"},
//                 {"Description",   "description"},
//                 {"Is Active",     "isActive"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new ExpenseSectionReport().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  EnquirySectionReport – Claims Section Report
// // ════════════════════════════════════════════════════════════
// class EnquirySectionReport extends BaseReportWindow {
//     public EnquirySectionReport() {
//         super("CLAIMS SECTION REPORT", "claim", "amount",
//             new String[][]{
//                 {"Claim ID",    "claimId"},
//                 {"Employee ID", "employeeId"},
//                 {"Status",      "status"},
//                 {"Approved By", "approvedBy"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new EnquirySectionReport().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  DonerSectionReport – Approval Report
// // ════════════════════════════════════════════════════════════
// class DonerSectionReport extends BaseReportWindow {
//     public DonerSectionReport() {
//         super("APPROVAL REPORT", "approval", "amount",
//             new String[][]{
//                 {"Approval ID",  "approvalId"},
//                 {"Claim ID",     "claimId"},
//                 {"Approver ID",  "approverId"},
//                 {"Status",       "status"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new DonerSectionReport().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  AccountSectionReport – Account Detail Report (Reimbursements)
// // ════════════════════════════════════════════════════════════
// class AccountSectionReport extends BaseReportWindow {
//     public AccountSectionReport() {
//         super("REIMBURSEMENTS REPORT", "reimbursement", "amount",
//             new String[][]{
//                 {"Reimb. ID",     "reimbId"},
//                 {"Claim ID",      "claimId"},
//                 {"Employee ID",   "employeeId"},
//                 {"Payment Mode",  "paymentMode"},
//                 {"Status",        "status"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new AccountSectionReport().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  AccountDetail – existing window referenced from menu
// //  (mirrors reimbursement table as "Account Detail")
// // ════════════════════════════════════════════════════════════
// class AccountDetail extends BaseReportWindow {
//     public AccountDetail() {
//         super("ACCOUNT DETAIL", "reimbursement", "amount",
//             new String[][]{
//                 {"Reimb. ID",    "reimbId"},
//                 {"Claim ID",     "claimId"},
//                 {"Employee ID",  "employeeId"},
//                 {"Status",       "status"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new AccountDetail().setVisible(true));
//     }
// }