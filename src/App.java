public class App {
    public static void main(String[] args) {
        try {
            // macOS: set app name in dock before anything starts
            System.setProperty("apple.awt.application.name", "Expense Reimbursement System");

            System.out.println("========================================");
            System.out.println("Expense Reimbursement System v1.0");
            System.out.println("========================================");

            // Initialize database (create tables and populate with sample data if empty)
            System.out.println("\n📊 Initializing database...");
            if (!DatabaseInitializer.initializeDatabase()) {
                System.err.println("❌ Database initialization failed!");
                System.exit(1);
            }

            System.out.println("\n🚀 Starting application...");

            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    new SignUpWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Error creating window: " + e.getMessage());
                    DatabaseInitializer.showInfoDialog("Error", "Error starting application:\n" + e.getMessage());
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fatal error: " + e.getMessage());
            try {
                DatabaseInitializer.showInfoDialog("Fatal Error", "Fatal error:\n" + e.getMessage());
            } catch (Exception ignored) {
            }
            System.exit(1);
        }
    }
}