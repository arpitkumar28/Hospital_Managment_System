package ui.theme;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContrastOptionPaneUITest {
    @BeforeAll
    static void installTheme() {
        AppTheme.install();
    }

    @Test
    void standardMessageAndConfirmationButtonsHaveReadableThemeColors() {
        assertTrue(contrastRatio(UIManager.getColor("Button.default.foreground"),
                UIManager.getColor("Button.default.background")) >= 4.5,
                "The navy default action must retain readable white text.");

        for (int messageType : List.of(
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.QUESTION_MESSAGE)) {
            JOptionPane pane = new JOptionPane("Test", messageType);
            assertDialogButtonsReadable(pane);
        }

        JOptionPane confirmation = new JOptionPane("Confirm?",
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
        assertDialogButtonsReadable(confirmation);
        List<String> labels = buttons(confirmation).stream().map(AbstractButton::getText).toList();
        assertTrue(labels.containsAll(List.of("Yes", "No", "Cancel")));
    }

    private static void assertDialogButtonsReadable(JOptionPane pane) {
        assertTrue(pane.getUI() instanceof ContrastOptionPaneUI);
        List<AbstractButton> buttons = buttons(pane);
        assertFalse(buttons.isEmpty());
        for (AbstractButton button : buttons) {
            assertTrue(contrastRatio(button.getForeground(), button.getBackground()) >= 4.5,
                    button.getText() + " must meet readable contrast in its default state");
            button.getModel().setRollover(true);
            assertTrue(contrastRatio(button.getForeground(), button.getBackground()) >= 4.5,
                    button.getText() + " must remain readable on hover");
            button.getModel().setRollover(false);
            button.getModel().setArmed(true);
            button.getModel().setPressed(true);
            assertTrue(contrastRatio(button.getForeground(), button.getBackground()) >= 4.5,
                    button.getText() + " must remain readable while pressed");
            button.getModel().setPressed(false);
            button.getModel().setArmed(false);
            assertEquals(AppTheme.FONT_BUTTON, button.getFont());
            assertTrue(button.isFocusable());
            button.setEnabled(false);
            assertTrue(contrastRatio(button.getForeground(), button.getBackground()) >= 4.5,
                    button.getText() + " must remain readable when disabled");
            button.setEnabled(true);
        }
    }

    private static List<AbstractButton> buttons(JComponent component) {
        List<AbstractButton> result = new ArrayList<>();
        collectButtons(component, result);
        return result;
    }

    private static void collectButtons(Component component, List<AbstractButton> result) {
        if (component instanceof AbstractButton button) result.add(button);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) collectButtons(child, result);
        }
    }

    private static double contrastRatio(Color foreground, Color background) {
        double lighter = Math.max(luminance(foreground), luminance(background));
        double darker = Math.min(luminance(foreground), luminance(background));
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double luminance(Color color) {
        double red = linearize(color.getRed() / 255.0);
        double green = linearize(color.getGreen() / 255.0);
        double blue = linearize(color.getBlue() / 255.0);
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    private static double linearize(double value) {
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }
}
