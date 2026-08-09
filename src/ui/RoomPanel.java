package ui;

import dao.RoomDAO;

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

    private RoomDAO roomDAO;

    private int selectedRoomId = -1;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RoomPanel() {

        roomDAO = new RoomDAO();

        setTitle("Hospital Management System - Room Management");

        setSize(900, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();

        loadRooms();

        setVisible(true);
    }


    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 247, 250)
        );


        // =================================================
        // HEADER
        // =================================================

        JLabel titleLabel =
                new JLabel(
                        "Room & Bed Management",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        titleLabel.setForeground(
                new Color(30, 55, 80)
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        10,
                        20,
                        10
                )
        );


        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
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


        // Room Number

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Room Number:"),
                gbc
        );


        roomNumberField =
                new JTextField(15);

        gbc.gridx = 1;

        formPanel.add(
                roomNumberField,
                gbc
        );


        // Room Type

        gbc.gridx = 2;

        formPanel.add(
                new JLabel("Room Type:"),
                gbc
        );


        roomTypeField =
                new JTextField(15);

        gbc.gridx = 3;

        formPanel.add(
                roomTypeField,
                gbc
        );


        // Price

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel("Price / Day:"),
                gbc
        );


        priceField =
                new JTextField(15);

        gbc.gridx = 1;

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


        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );


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

        tablePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
        );

        tablePanel.setBackground(
                new Color(245, 247, 250)
        );

        tablePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        mainPanel.add(
                tablePanel,
                BorderLayout.SOUTH
        );


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
                    roomDAO.addRoom(
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
                    roomDAO.updateRoom(
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
                roomDAO.deleteRoom(
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
                roomDAO.getAllRooms();


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