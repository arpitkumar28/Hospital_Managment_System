package ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/** Shared visual tokens and Look & Feel configuration for the Swing application. */
public final class AppTheme {

    public static final Color PRIMARY_COLOR = new Color(22, 78, 99);
    public static final Color SECONDARY_COLOR = new Color(36, 128, 140);
    public static final Color ACCENT_COLOR = new Color(13, 148, 136);
    public static final Color BACKGROUND_COLOR = new Color(243, 247, 250);
    public static final Color SURFACE_COLOR = Color.WHITE;
    public static final Color SIDEBAR_COLOR = new Color(20, 53, 70);
    public static final Color TEXT_PRIMARY = new Color(31, 48, 61);
    public static final Color TEXT_SECONDARY = new Color(102, 119, 132);
    public static final Color BORDER_COLOR = new Color(222, 231, 236);
    public static final Color SUCCESS_COLOR = new Color(27, 128, 89);
    public static final Color WARNING_COLOR = new Color(181, 112, 20);
    public static final Color ERROR_COLOR = new Color(181, 59, 59);
    public static final Color INFO_COLOR = new Color(40, 112, 166);

    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;

    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 10;
    public static final int RADIUS_LG = 16;

    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    public static final Font FONT_SUBHEADING = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    private AppTheme() { }

    /** Installs the application's FlatLaf theme before Swing components are created. */
    public static void install() {
        FlatLightLaf.setup();
        UIManager.put("defaultFont", FONT_BODY);
        UIManager.put("Component.arc", RADIUS_SM);
        UIManager.put("Button.arc", RADIUS_SM);
        UIManager.put("TextComponent.arc", RADIUS_SM);
        UIManager.put("Component.focusColor", ACCENT_COLOR);
        UIManager.put("Component.borderColor", BORDER_COLOR);
    }

    public static Border pagePadding() {
        return BorderFactory.createEmptyBorder(SPACE_XL, SPACE_XL, SPACE_XL, SPACE_XL);
    }
}
