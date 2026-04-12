// import javax.swing.SwingUtilities;

// // ════════════════════════════════════════════════════════════
// //  DonerSectionQuery – Expense Query
// // ════════════════════════════════════════════════════════════
// class DonerSectionQuery extends BaseQueryWindow {
//     public DonerSectionQuery() {
//         super("EXPENSE QUERY", "expensecategory",
//             new String[][]{
//                 {"Category ID",   "categoryId"},
//                 {"Category Name", "categoryName"},
//                 {"Description",   "description"},
//                 {"Is Active",     "isActive"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new DonerSectionQuery().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  SalarySectionQuery – Approval Section Query
// // ════════════════════════════════════════════════════════════
// class SalarySectionQuery extends BaseQueryWindow {
//     public SalarySectionQuery() {
//         super("APPROVAL SECTION QUERY", "approval",
//             new String[][]{
//                 {"Approval ID",   "approvalId"},
//                 {"Claim ID",      "claimId"},
//                 {"Approver ID",   "approverId"},
//                 {"Status",        "status"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new SalarySectionQuery().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  EnquirySectionQuery – Claims Section Query
// // ════════════════════════════════════════════════════════════
// class EnquirySectionQuery extends BaseQueryWindow {
//     public EnquirySectionQuery() {
//         super("CLAIMS SECTION QUERY", "claim",
//             new String[][]{
//                 {"Claim ID",     "claimId"},
//                 {"Employee ID",  "employeeId"},
//                 {"Category ID",  "categoryId"},
//                 {"Status",       "status"},
//                 {"Approved By",  "approvedBy"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new EnquirySectionQuery().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  AccountSectionQuerry – Expense Categories Section Query
// // ════════════════════════════════════════════════════════════
// class AccountSectionQuerry extends BaseQueryWindow {
//     public AccountSectionQuerry() {
//         super("EXPENSE CATEGORIES SECTION QUERY", "expensecategory",
//             new String[][]{
//                 {"Category ID",   "categoryId"},
//                 {"Category Name", "categoryName"},
//                 {"Budget Limit",  "budgetLimit"},
//                 {"Is Active",     "isActive"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new AccountSectionQuerry().setVisible(true));
//     }
// }

// // ════════════════════════════════════════════════════════════
// //  EmployeeSectionQuery – Employee Section Query
// // ════════════════════════════════════════════════════════════
// class EmployeeSectionQuery extends BaseQueryWindow {
//     public EmployeeSectionQuery() {
//         super("EMPLOYEE SECTION QUERY", "employee",
//             new String[][]{
//                 {"Employee ID",   "employeeId"},
//                 {"Name",          "employeename"},
//                 {"Department ID", "companyId"},
//                 {"Post/Role",     "postName"},
//                 {"Contact No",    "contactno"}
//             });
//     }
//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new EmployeeSectionQuery().setVisible(true));
//     }
// }