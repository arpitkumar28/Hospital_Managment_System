package ui;

import dao.BedDAO;
import dao.RoomDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BedPanel extends JFrame {

    private JComboBox<RoomItem> roomComboBox;
    private JTextField bedNumberField;
    private JComboBox<String> statusComboBox;

    private JTable bedTable;
    private DefaultTableModel tableModel;

    private BedDAO bedDAO;
    private RoomDAO roomDAO;

    private int selectedBedId = -1;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BedPanel() {

        bedDAO = new BedDAO();
        roomDAO = new RoomDAO();

        setTitle("Hospital Management System - Bed Management");

        setSize(950, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();

        loadRooms();

        loadBeds();

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
                        "Bed Management",
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


        // =================================================
        // ROOM
        // =================================================

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Room:"),
                gbc
        );


        roomComboBox =
                new JComboBox<>();

        roomComboBox.setPreferredSize(
                new Dimension(
                        220,
                        30
                )
        );

        gbc.gridx = 1;

        formPanel.add(
                roomComboBox,
                gbc
        );


        // =================================================
        // BED NUMBER
        // =================================================

        gbc.gridx = 2;

        formPanel.add(
                new JLabel("Bed Number:"),
                gbc
        );


        bedNumberField =
                new JTextField();

        bedNumberField.setPreferredSize(
                new Dimension(
                        220,
                        30
                )
        );

        gbc.gridx = 3;

        formPanel.add(
                bedNumberField,
                gbc
        );


        // =================================================
        // STATUS
        // =================================================

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel("Status:"),
                gbc
        );


        statusComboBox =
                new JComboBox<>(
                        new String[]{
                                "AVAILABLE",
                                "OCCUPIED"
                        }
                );

        gbc.gridx = 1;

        formPanel.add(
                statusComboBox,
                gbc
        );


        // =================================================
        // BUTTONS
        // =================================================

        JButton addButton =
                new JButton("Add Bed");

        JButton updateButton =
                new JButton("Update");

        JButton deleteButton =
                new JButton("Delete");

        JButton clearButton =
                new JButton("Clear");

        JButton refreshButton =
                new JButton("Refresh");


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


        gbc.gridx = 0;
        gbc.gridy = 3;

        formPanel.add(
                refreshButton,
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
                                "Bed ID",
                                "Room ID",
                                "Room Number",
                                "Bed Number",
                                "Status"
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


        bedTable =
                new JTable(tableModel);

        bedTable.setRowHeight(30);

        bedTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        bedTable
                );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Bed List"
                )
        );


        JPanel tablePanel =
                new JPanel(
                        new BorderLayout()
                );

        tablePanel.setBackground(
                new Color(245, 247, 250)
        );

        tablePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
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
        // BUTTON EVENTS
        // =================================================

        addButton.addActionListener(
                e -> addBed()
        );


        updateButton.addActionListener(
                e -> updateBed()
        );


        deleteButton.addActionListener(
                e -> deleteBed()
        );


        clearButton.addActionListener(
                e -> clearFields()
        );


        refreshButton.addActionListener(
                e -> {

                    loadRooms();
                    loadBeds();
                }
        );


        // =================================================
        // TABLE SELECTION
        // =================================================

        bedTable.getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                selectBed();
                            }
                        }
                );


        add(
                mainPanel
        );
    }


    // =====================================================
    // LOAD ROOMS
    // =====================================================

    private void loadRooms() {

        roomComboBox.removeAllItems();

        List<Object[]> rooms =
                roomDAO.getAllRooms();


        for (Object[] room : rooms) {

            int roomId =
                    Integer.parseInt(
                            room[0].toString()
                    );

            String roomNumber =
                    room[1].toString();

            String roomType =
                    room[2].toString();


            roomComboBox.addItem(
                    new RoomItem(
                            roomId,
                            roomNumber,
                            roomType
                    )
            );
        }
    }


    // =====================================================
    // LOAD BEDS
    // =====================================================

    private void loadBeds() {

        tableModel.setRowCount(0);

        List<Object[]> beds =
                bedDAO.getAllBeds();


        for (Object[] bed : beds) {

            tableModel.addRow(
                    bed
            );
        }
    }


    // =====================================================
    // ADD BED
    // =====================================================

    private void addBed() {

        RoomItem selectedRoom =
                (RoomItem)
                        roomComboBox
                                .getSelectedItem();


        String bedNumber =
                bedNumberField
                        .getText()
                        .trim();


        if (selectedRoom == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (bedNumber.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a bed number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        boolean success =
                bedDAO.addBed(
                        selectedRoom.roomId,
                        bedNumber
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bed added successfully."
            );

            clearFields();

            loadBeds();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add bed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // UPDATE BED
    // =====================================================

    private void updateBed() {

        if (selectedBedId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a bed first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        RoomItem selectedRoom =
                (RoomItem)
                        roomComboBox
                                .getSelectedItem();


        String bedNumber =
                bedNumberField
                        .getText()
                        .trim();


        String status =
                statusComboBox
                        .getSelectedItem()
                        .toString();


        if (selectedRoom == null) {

            return;
        }


        if (bedNumber.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a bed number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        boolean success =
                bedDAO.updateBed(
                        selectedBedId,
                        selectedRoom.roomId,
                        bedNumber,
                        status
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bed updated successfully."
            );

            clearFields();

            loadBeds();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update bed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // DELETE BED
    // =====================================================

    private void deleteBed() {

        if (selectedBedId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a bed first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this bed?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                result != JOptionPane.YES_OPTION
        ) {

            return;
        }


        boolean success =
                bedDAO.deleteBed(
                        selectedBedId
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bed deleted successfully."
            );

            clearFields();

            loadBeds();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete bed.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // SELECT BED
    // =====================================================

    private void selectBed() {

        int row =
                bedTable.getSelectedRow();


        if (row == -1) {

            return;
        }


        selectedBedId =
                Integer.parseInt(
                        bedTable
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );


        int roomId =
                Integer.parseInt(
                        bedTable
                                .getValueAt(
                                        row,
                                        1
                                )
                                .toString()
                );


        String bedNumber =
                bedTable
                        .getValueAt(
                                row,
                                3
                        )
                        .toString();


        String status =
                bedTable
                        .getValueAt(
                                row,
                                4
                        )
                        .toString();


        // Select matching room

        for (
                int i = 0;
                i < roomComboBox.getItemCount();
                i++
        ) {

            RoomItem room =
                    roomComboBox
                            .getItemAt(i);


            if (
                    room.roomId == roomId
            ) {

                roomComboBox
                        .setSelectedIndex(i);

                break;
            }
        }


        bedNumberField.setText(
                bedNumber
        );


        statusComboBox.setSelectedItem(
                status
        );
    }


    // =====================================================
    // CLEAR FIELDS
    // =====================================================

    private void clearFields() {

        bedNumberField.setText("");

        selectedBedId = -1;

        bedTable.clearSelection();

        if (
                roomComboBox.getItemCount() > 0
        ) {

            roomComboBox.setSelectedIndex(0);
        }

        statusComboBox.setSelectedItem(
                "AVAILABLE"
        );
    }


    // =====================================================
    // ROOM ITEM
    // =====================================================

    private static class RoomItem {

        private int roomId;

        private String roomNumber;

        private String roomType;


        public RoomItem(
                int roomId,
                String roomNumber,
                String roomType) {

            this.roomId = roomId;

            this.roomNumber =
                    roomNumber;

            this.roomType =
                    roomType;
        }


        @Override
        public String toString() {

            return roomNumber +
                    " - " +
                    roomType;
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                BedPanel::new
        );
    }
}