import javax.swing.*;
/**
 * BookingSectionQuerry – Managers Section Query
 */
public class BookingSectionQuerry extends BaseQueryWindow {

    public BookingSectionQuerry() {
        super("MANAGERS SECTION QUERY", "manager",
            new String[][]{
                {"Manager ID",   "managerId"},
                {"Manager Name", "managerName"},
                {"Department",   "department"},
                {"Status",       "status"}
            });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingSectionQuerry().setVisible(true));
    }
}