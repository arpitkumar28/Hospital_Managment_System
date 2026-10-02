import javax.swing.SwingUtilities;

import ui.LoginFrame;
import ui.theme.AppTheme;

public class Main {

    public static void main(String[] args) {

        AppTheme.install();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
