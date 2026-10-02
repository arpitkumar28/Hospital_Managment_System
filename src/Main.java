import javax.swing.SwingUtilities;

import ui.LoginFrame;
import ui.theme.AppTheme;
import service.InitialAdminProvisioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        AppTheme.install();
        String setupMessage = null;
        try {
            new InitialAdminProvisioner().provisionIfConfigured();
        } catch (InitialAdminProvisioner.ProvisioningException exception) {
            LOGGER.error("Initial administrator setup could not be completed.", exception);
            setupMessage = exception.getMessage();
        }
        String warning = setupMessage;
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
            if (warning != null) {
                JOptionPane.showMessageDialog(null, warning, "Administrator setup", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}
