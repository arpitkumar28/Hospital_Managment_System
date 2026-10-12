package ui;

import service.RoomService;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JFrame {

    private JTextField roomNumberField;
    private JTextField roomTypeField;
    private JTextField priceField;

    private JTable roomTable;
    private DefaultTableModel tableModel;

    private RoomService roomService;

    private int selectedRoomId = -1;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RoomPanel() {

        roomService = new RoomService();

        setTitle("Hospital Management System - Room Management");

        setSize(900, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();
        AppTheme.styleLegacyWindow(getContentPane());

        loadRooms();

        setVisible(true);
    }


    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(AppTheme.BACKGROUND_COLOR);
        mainPanel.setBorder(AppTheme.pagePadding());


        // =================================================
        // HEADER
        // =================================================

        JLabel titleLabel =
                new JLabel(
                        "Room & Bed Management",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(AppTheme.SPACE_MD, 0, AppTheme.SPACE_MD, 0)
        );




        // =================================================
        // FORM
        // =================================================

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(
                Color.WHITE
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        8,
                        8,
                        8
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;


        // Room Number

        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel roomNumberLabel = new JLabel("Room Number:");
        formPanel.add(roomNumberLabel, gbc);


        roomNumberField =
                new JTextField(14);
        roomNumberLabel.setLabelFor(roomNumberField);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                roomNumberField,
                gbc
        );


        // Room Type

        gbc.gridx = 2;

        JLabel roomTypeLabel = new JLabel("Room Type:");
        formPanel.add(roomTypeLabel, gbc);


        roomTypeField =
                new JTextField(14);
        roomTypeLabel.setLabelFor(roomTypeField);

        gbc.gridx = 3;
        gbc.weightx = 1;

        formPanel.add(
                roomTypeField,
                gbc
        );


        // Price

        gbc.gridx = 0;
        gbc.gridy = 1;

        JLabel priceLabel = new JLabel("Price / Day:");
        formPanel.add(priceLabel, gbc);


        priceField =
                new JTextField(12);
        priceLabel.setLabelFor(priceField);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(
                priceField,
                gbc
        );


        // =================================================
        // BUTTONS
        // =================================================

        JButton addButton =
                new JButton("Add Room");

        JButton updateButton =
                new JButton("Update Room");

        JButton deleteButton =
                new JButton("Delete Room");

        JButton clearButton =
                new JButton("Clear");


        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(
                addButton,
                gbc
        );


        gbc.gridx = 1;

        formPanel.add(
                updateButton,
                gbc
        );


        gbc.gridx = 2;

        formPanel.add(
                deleteButton,
                gbc
        );


        gbc.gridx = 3;

        formPanel.add(
                clearButton,
                gbc
        );


        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(top, BorderLayout.NORTH);


        // =================================================
        // TABLE
        // =================================================

        tableModel =
                new DefaultTableModel(
                        new String[]{
                                "ID",
                                "Room Number",
                                "Room Type",
                                "Price / Day"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        roomTable =
                new JTable(tableModel);

        roomTable.setRowHeight(30);

        roomTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollPane =
                new JScrollPane(roomTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Room List"
                )
        );


        JPanel tablePanel =
                new JPanel(
                        new BorderLayout()
                );

        tablePanel.setBorder(BorderFactory.createEmptyBorder(AppTheme.SPACE_MD, 0, 0, 0));

        tablePanel.setBackground(
                new Color(245, 247, 250)
        );

        tablePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        mainPanel.add(tablePanel, BorderLayout.CENTER);
        setMinimumSize(new Dimension(760, 520));
        scrollPane.setPreferredSize(new Dimension(0, 280));
        roomTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] widths = {60, 180, 230, 150};
        for (int column = 0; column < widths.length; column++) {
            roomTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }


        // =================================================
        // BUTTON ACTIONS
        // =================================================

        addButton.addActionListener(
                e -> addRoom()
        );


        updateButton.addActionListener(
                e -> updateRoom()
        );


        deleteButton.addActionListener(
                e -> deleteRoom()
        );


        clearButton.addActionListener(
                e -> clearFields()
        );


        // =================================================
        // TABLE SELECTION
        // =================================================

        roomTable.getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selectRoom();
                            }
                        }
                );


        add(
                mainPanel
        );
    }


    // =====================================================
    // ADD ROOM
    // =====================================================

    private void addRoom() {

        String roomNumber =
                roomNumberField
                        .getText()
                        .trim();

        String roomType =
                roomTypeField
                        .getText()
                        .trim();

        String priceText =
                priceField
                        .getText()
                        .trim();


        if (
                roomNumber.isEmpty()
                        || roomType.isEmpty()
                        || priceText.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            double price =
                    Double.parseDouble(
                            priceText
                    );


            if (price < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Price cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }


            boolean success =
                    roomService.addRoom(
                            roomNumber,
                            roomType,
                            price
                    );


            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Room added successfully."
                );

                clearFields();

                loadRooms();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to add room.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Price must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    // =====================================================
    // UPDATE ROOM
    // =====================================================

    private void updateRoom() {

        if (selectedRoomId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        String roomNumber =
                roomNumberField
                        .getText()
                        .trim();

        String roomType =
                roomTypeField
                        .getText()
                        .trim();

        String priceText =
                priceField
                        .getText()
                        .trim();


        if (
                roomNumber.isEmpty()
                        || roomType.isEmpty()
                        || priceText.isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        try {

            double price =
                    Double.parseDouble(
                            priceText
                    );


            boolean success =
                    roomService.updateRoom(
                            selectedRoomId,
                            roomNumber,
                            roomType,
                            price
                    );


            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Room updated successfully."
                );

                clearFields();

                loadRooms();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to update room.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Price must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }


    // =====================================================
    // DELETE ROOM
    // =====================================================

    private void deleteRoom() {

        if (selectedRoomId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this room?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                result != JOptionPane.YES_OPTION
        ) {

            return;
        }


        boolean success =
                roomService.deleteRoom(
                        selectedRoomId
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Room deleted successfully."
            );

            clearFields();

            loadRooms();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete room.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // LOAD ROOMS
    // =====================================================

    private void loadRooms() {

        tableModel.setRowCount(0);


        List<Object[]> rooms =
                roomService.getAllRooms();


        for (Object[] room : rooms) {

            tableModel.addRow(room);
        }
    }


    // =====================================================
    // SELECT ROOM
    // =====================================================

    private void selectRoom() {

        int row =
                roomTable.getSelectedRow();


        if (row == -1) {

            return;
        }


        selectedRoomId =
                Integer.parseInt(
                        roomTable
                                .getValueAt(row, 0)
                                .toString()
                );


        roomNumberField.setText(
                roomTable
                        .getValueAt(row, 1)
                        .toString()
        );


        roomTypeField.setText(
                roomTable
                        .getValueAt(row, 2)
                        .toString()
        );


        priceField.setText(
                roomTable
                        .getValueAt(row, 3)
                        .toString()
        );
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        roomNumberField.setText("");

        roomTypeField.setText("");

        priceField.setText("");

        selectedRoomId = -1;

        roomTable.clearSelection();
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                RoomPanel::new
        );
    }
}
