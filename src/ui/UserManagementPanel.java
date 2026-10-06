package ui;

import model.AuthenticatedUser;
import model.StaffAccountSummary;
import model.UserRole;
import model.UserStatus;
import security.AuthorizationService;
import security.SessionManager;
import service.AdminUserService;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Administrator-only staff account screen; all account operations go through AdminUserService. */
public final class UserManagementPanel extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementPanel.class);
    private static final String TABLE_CARD = "table";
    private static final String EMPTY_CARD = "empty";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault());

    private final AdminUserService users;
    private final AuthenticatedUser actor;
    private final JTextField searchField = new JTextField(22);
    private final JComboBox<String> roleFilter = new JComboBox<>(new String[]{"All roles", "ADMIN", "RECEPTIONIST", "DOCTOR", "ACCOUNTANT"});
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All statuses", "ACTIVE", "INACTIVE", "LOCKED", "PENDING"});
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton addButton = new PrimaryButton("+ Add User");
    private final JLabel activity = new JLabel(" ");
    private final CardLayout resultsLayout = new CardLayout();
    private final JPanel results = new JPanel(resultsLayout);
    private final UserTableModel tableModel = new UserTableModel();
    private final JTable table = new JTable(tableModel);
    private final JPanel emptyState = new JPanel(new GridBagLayout());
    private final Timer searchTimer;
    private List<StaffAccountSummary> loadedUsers = List.of();
    private boolean busy;

    public UserManagementPanel() {
        this(new AdminUserService(), SessionManager.INSTANCE, new AuthorizationService());
    }

    UserManagementPanel(AdminUserService users, SessionManager sessions, AuthorizationService authorization) {
        super("Hospital Management System - User Management");
        this.users = users;
        this.actor = sessions.getCurrentUser().orElse(null);
        authorization.requireModule(actor, AuthorizationService.USER_MANAGEMENT);

        searchTimer = new Timer(250, event -> applyFilters());
        searchTimer.setRepeats(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 650));
        setSize(1370, 790);
        setLocationRelativeTo(null);
        buildScreen();
        loadUsers();
        setVisible(true);
    }

    private void buildScreen() {
        JPanel root = new JPanel(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        root.setBackground(AppTheme.BACKGROUND_COLOR);
        root.setBorder(AppTheme.pagePadding());
        root.add(buildHeading(), BorderLayout.NORTH);
        root.add(buildDirectory(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildHeading() {
        JPanel heading = new JPanel(new BorderLayout(AppTheme.SPACE_MD, 0));
        heading.setOpaque(false);
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new javax.swing.BoxLayout(copy, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("User Management");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Manage staff accounts, roles and access");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        copy.add(title);
        copy.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_XS));
        copy.add(subtitle);
        heading.add(copy, BorderLayout.WEST);
        heading.add(addButton, BorderLayout.EAST);
        addButton.addActionListener(event -> showCreateDialog());
        return heading;
    }

    private JPanel buildDirectory() {
        JPanel directory = new JPanel(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        directory.setBackground(AppTheme.SURFACE_COLOR);
        directory.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(AppTheme.SPACE_MD, AppTheme.SPACE_MD, AppTheme.SPACE_MD, AppTheme.SPACE_MD)));

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.SPACE_SM, 0));
        filters.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", "Search name, username or email");
        roleFilter.setFont(AppTheme.FONT_BODY);
        statusFilter.setFont(AppTheme.FONT_BODY);
        filters.add(searchField);
        filters.add(roleFilter);
        filters.add(statusFilter);
        filters.add(refreshButton);
        directory.add(filters, BorderLayout.NORTH);

        table.setRowHeight(42);
        table.setFont(AppTheme.FONT_BODY);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(AppTheme.FONT_SUBHEADING);
        table.getTableHeader().setBackground(new Color(239, 245, 247));
        table.getTableHeader().setForeground(AppTheme.TEXT_PRIMARY);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionEditor(this::showActions));
        setColumnWidths(table.getColumnModel());

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setOpaque(false);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        buildEmptyState();
        results.setOpaque(false);
        results.add(tableCard, TABLE_CARD);
        results.add(emptyState, EMPTY_CARD);
        resultsLayout.show(results, EMPTY_CARD);
        directory.add(results, BorderLayout.CENTER);
        directory.add(activity, BorderLayout.SOUTH);

        refreshButton.addActionListener(event -> loadUsers());
        roleFilter.addActionListener(event -> applyFilters());
        statusFilter.addActionListener(event -> applyFilters());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { scheduleFilter(); }
            @Override public void removeUpdate(DocumentEvent event) { scheduleFilter(); }
            @Override public void changedUpdate(DocumentEvent event) { scheduleFilter(); }
        });
        return directory;
    }

    private void buildEmptyState() {
        emptyState.setOpaque(false);
        JPanel message = new JPanel();
        message.setOpaque(false);
        message.setLayout(new javax.swing.BoxLayout(message, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("No users found", SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_HEADING);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel details = new JLabel("Try changing the search or filters, or add a staff account.", SwingConstants.CENTER);
        details.setFont(AppTheme.FONT_BODY);
        details.setForeground(AppTheme.TEXT_SECONDARY);
        details.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton reload = new JButton("Refresh users");
        reload.setAlignmentX(Component.CENTER_ALIGNMENT);
        reload.addActionListener(event -> loadUsers());
        message.add(title);
        message.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_SM));
        message.add(details);
        message.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_MD));
        message.add(reload);
        emptyState.add(message);
    }

    private static void setColumnWidths(TableColumnModel columns) {
        int[] widths = {165, 115, 190, 125, 125, 105, 155, 95};
        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void scheduleFilter() { searchTimer.restart(); }

    private void loadUsers() {
        runOperation("Loading users…", "Unable to load users.", users::listUsers, loaded -> {
            loadedUsers = loaded;
            applyFilters();
        });
    }

    private void applyFilters() {
        if (busy) return;
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        String role = (String) roleFilter.getSelectedItem();
        String status = (String) statusFilter.getSelectedItem();
        List<StaffAccountSummary> filtered = new ArrayList<>();
        for (StaffAccountSummary user : loadedUsers) {
            boolean matchesQuery = query.isEmpty() || user.fullName().toLowerCase(Locale.ROOT).contains(query)
                    || user.username().toLowerCase(Locale.ROOT).contains(query)
                    || user.email().toLowerCase(Locale.ROOT).contains(query);
            boolean matchesRole = role == null || role.equals("All roles") || user.role().name().equals(role);
            boolean matchesStatus = status == null || status.equals("All statuses") || user.status().name().equals(status);
            if (matchesQuery && matchesRole && matchesStatus) filtered.add(user);
        }
        tableModel.setUsers(filtered);
        resultsLayout.show(results, filtered.isEmpty() ? EMPTY_CARD : TABLE_CARD);
        activity.setText(loadedUsers.size() + (loadedUsers.size() == 1 ? " account" : " accounts")
                + (filtered.size() == loadedUsers.size() ? "" : " · " + filtered.size() + " shown"));
    }

    private void showCreateDialog() {
        JTextField fullName = new JTextField(24);
        JTextField username = new JTextField(24);
        JTextField email = new JTextField(24);
        JTextField phone = new JTextField(24);
        JComboBox<UserRole> role = new JComboBox<>(new UserRole[]{UserRole.RECEPTIONIST, UserRole.DOCTOR, UserRole.ACCOUNTANT});
        JLabel activeStatus = new JLabel("Active");
        JPasswordField password = new JPasswordField(24);
        JPasswordField confirm = new JPasswordField(24);
        JPanel form = formPanel(new String[]{"Full Name", "Username", "Email", "Phone", "Role", "Status", "Password", "Confirm Password"},
                new Component[]{fullName, username, email, phone, role, activeStatus, password, confirm});
        int answer = JOptionPane.showConfirmDialog(this, form, "Add Staff User", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) {
            clear(password.getPassword()); clear(confirm.getPassword()); return;
        }
        char[] pass = password.getPassword();
        char[] confirmation = confirm.getPassword();
        runOperation("Creating account…", "Unable to create the account.", () -> users.createUser(
                fullName.getText(), username.getText(), email.getText(), phone.getText(),
                (UserRole) role.getSelectedItem(), pass, confirmation), id -> {
            showMessage("Account created successfully.", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        });
    }

    private static JPanel formPanel(String[] labels, Component[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(AppTheme.SPACE_SM, AppTheme.SPACE_SM, AppTheme.SPACE_SM, AppTheme.SPACE_SM));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.LINE_START;
        for (int row = 0; row < labels.length; row++) {
            c.gridx = 0; c.gridy = row; c.weightx = 0; c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(labels[row] + ":"), c);
            c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
            panel.add(fields[row], c);
        }
        return panel;
    }

    private void showActions(StaffAccountSummary user) {
        List<String> options = new ArrayList<>();
        if (user.status() == UserStatus.PENDING) {
            options.add("Approve"); options.add("Reject / Deactivate");
        } else if (user.status() == UserStatus.ACTIVE) {
            if (!isSelf(user)) { options.add("Deactivate"); options.add("Lock"); }
            options.add("Change Role"); options.add("Reset Password");
        } else if (user.status() == UserStatus.INACTIVE) {
            options.add("Activate"); options.add("Change Role"); options.add("Reset Password");
        } else if (user.status() == UserStatus.LOCKED) {
            options.add("Unlock"); options.add("Deactivate"); options.add("Change Role"); options.add("Reset Password");
        }
        if (options.isEmpty()) return;
        Object selected = JOptionPane.showInputDialog(this, user.fullName(), "Account Actions",
                JOptionPane.PLAIN_MESSAGE, null, options.toArray(), options.get(0));
        if (!(selected instanceof String action)) return;
        switch (action) {
            case "Approve" -> approve(user);
            case "Reject / Deactivate", "Deactivate" -> confirmAndRun(action, "This user will no longer be able to sign in.",
                    () -> {
                        if (action.equals("Reject / Deactivate")) reject(user.userId());
                        else users.deactivate(user.userId());
                    });
            case "Activate" -> confirmAndRun(action, "This user will be able to sign in again.", () -> users.activate(user.userId()));
            case "Lock" -> confirmAndRun(action, "This user will not be able to sign in until unlocked.", () -> users.lock(user.userId()));
            case "Unlock" -> confirmAndRun(action, "This user will be able to sign in again.", () -> users.unlock(user.userId()));
            case "Change Role" -> changeRole(user);
            case "Reset Password" -> resetPassword(user);
            default -> { }
        }
    }

    private void approve(StaffAccountSummary user) {
        JComboBox<UserRole> role = new JComboBox<>(new UserRole[]{UserRole.RECEPTIONIST, UserRole.DOCTOR, UserRole.ACCOUNTANT});
        role.setSelectedItem(user.role() == UserRole.ADMIN ? UserRole.RECEPTIONIST : user.role());
        JPanel form = formPanel(new String[]{"Approved role"}, new Component[]{role});
        if (JOptionPane.showConfirmDialog(this, form, "Approve Pending User", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        UserRole selectedRole = (UserRole) role.getSelectedItem();
        confirmAndRun("Approve", "The account will become active.", () -> users.approve(user.userId(), selectedRole));
    }

    private void reject(long id) throws AdminUserService.AdminOperationException { users.reject(id); }

    private void changeRole(StaffAccountSummary user) {
        JComboBox<UserRole> role = new JComboBox<>(UserRole.values());
        role.setSelectedItem(user.role());
        JPanel form = formPanel(new String[]{"Current role", "New role"}, new Component[]{new JLabel(user.role().name()), role});
        if (JOptionPane.showConfirmDialog(this, form, "Change User Role", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        UserRole selectedRole = (UserRole) role.getSelectedItem();
        if (selectedRole == user.role()) return;
        confirmAndRun("Change Role", "Change this user's role to " + selectedRole + "?",
                () -> users.changeRole(user.userId(), selectedRole));
    }

    private void resetPassword(StaffAccountSummary user) {
        JPasswordField password = new JPasswordField(24);
        JPasswordField confirm = new JPasswordField(24);
        JPanel form = formPanel(new String[]{"New Password", "Confirm Password"}, new Component[]{password, confirm});
        if (JOptionPane.showConfirmDialog(this, form, "Reset Password", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            clear(password.getPassword()); clear(confirm.getPassword()); return;
        }
        char[] pass = password.getPassword();
        char[] confirmation = confirm.getPassword();
        runOperation("Resetting password…", "Unable to update the user.", () -> {
            if (!java.util.Arrays.equals(pass, confirmation)) {
                clear(pass); clear(confirmation);
                throw new AdminUserService.AdminOperationException("Passwords do not match.");
            }
            users.resetPassword(user.userId(), pass);
            clear(confirmation);
            return null;
        }, ignored -> {
            showMessage("Password reset successfully.", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        });
    }

    private void confirmAndRun(String action, String detail, CheckedOperation operation) {
        if (JOptionPane.showConfirmDialog(this, detail, action + " User?", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) return;
        runOperation(action + " user…", "Unable to update the user.", () -> { operation.run(); return null; }, ignored -> {
            showMessage("User updated successfully.", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        });
    }

    private <T> void runOperation(String loading, String fallback, Callable<T> operation, Consumer<T> success) {
        if (busy) return;
        setBusy(true, loading);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return operation.call(); }
            @Override protected void done() {
                try {
                    T result = get();
                    setBusy(false, " ");
                    success.accept(result);
                } catch (Exception exception) {
                    setBusy(false, " ");
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    if (cause instanceof AdminUserService.AdminOperationException serviceError) {
                        showMessage(serviceError.getMessage(), JOptionPane.ERROR_MESSAGE);
                    } else {
                        LOGGER.error("User management operation failed.", cause);
                        showMessage(fallback, JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void setBusy(boolean value, String message) {
        busy = value;
        addButton.setEnabled(!value);
        refreshButton.setEnabled(!value);
        searchField.setEnabled(!value);
        roleFilter.setEnabled(!value);
        statusFilter.setEnabled(!value);
        table.setEnabled(!value);
        activity.setText(message);
        if (!value && !loadedUsers.isEmpty()) applyFilters();
    }

    private boolean isSelf(StaffAccountSummary user) { return actor != null && actor.userId() == user.userId(); }

    private void showMessage(String message, int type) {
        JOptionPane.showMessageDialog(this, message, "User Management", type);
    }

    private static void clear(char[] value) { if (value != null) java.util.Arrays.fill(value, '\0'); }

    private static String formatDate(java.time.Instant instant) { return instant == null ? "Never" : DATE_FORMAT.format(instant); }

    private interface CheckedOperation { void run() throws AdminUserService.AdminOperationException; }

    private final class ActionRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ActionRenderer() { setText("Manage"); setFont(AppTheme.FONT_SMALL); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                                  boolean focus, int row, int column) {
            setText("Manage");
            setEnabled(!busy);
            return this;
        }
    }

    private final class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Manage");
        private int modelRow;
        ActionEditor(Consumer<StaffAccountSummary> action) {
            button.setFont(AppTheme.FONT_SMALL);
            button.addActionListener((ActionEvent event) -> {
                StaffAccountSummary user = tableModel.userAt(modelRow);
                fireEditingStopped();
                action.accept(user);
            });
        }
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
            modelRow = table.convertRowIndexToModel(row);
            return button;
        }
        @Override public Object getCellEditorValue() { return "Manage"; }
    }

    private static final class StatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                                  boolean focus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            if (!selected && value instanceof UserStatus status) {
                switch (status) {
                    case ACTIVE -> { label.setBackground(new Color(226, 244, 235)); label.setForeground(AppTheme.SUCCESS_COLOR); }
                    case INACTIVE -> { label.setBackground(new Color(237, 240, 242)); label.setForeground(AppTheme.TEXT_SECONDARY); }
                    case LOCKED -> { label.setBackground(new Color(252, 233, 231)); label.setForeground(AppTheme.ERROR_COLOR); }
                    case PENDING -> { label.setBackground(new Color(255, 244, 220)); label.setForeground(AppTheme.WARNING_COLOR); }
                }
            }
            label.setText(value == null ? "" : value.toString());
            return label;
        }
    }

    private static final class UserTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {"Full Name", "Username", "Email", "Phone", "Role", "Status", "Last Login", "Actions"};
        private List<StaffAccountSummary> users = List.of();
        void setUsers(List<StaffAccountSummary> users) { this.users = List.copyOf(users); fireTableDataChanged(); }
        StaffAccountSummary userAt(int row) { return users.get(row); }
        @Override public int getRowCount() { return users.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int column) { return COLUMNS[column]; }
        @Override public Class<?> getColumnClass(int column) { return switch (column) { case 4 -> UserRole.class; case 5 -> UserStatus.class; default -> String.class; }; }
        @Override public Object getValueAt(int row, int column) {
            StaffAccountSummary user = users.get(row);
            return switch (column) {
                case 0 -> user.fullName(); case 1 -> user.username(); case 2 -> user.email(); case 3 -> user.phone();
                case 4 -> user.role(); case 5 -> user.status(); case 6 -> formatDate(user.lastLogin()); default -> "Manage";
            };
        }
        @Override public boolean isCellEditable(int row, int column) { return column == 7; }
    }
}
