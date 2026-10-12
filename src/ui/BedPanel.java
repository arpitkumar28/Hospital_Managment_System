package ui;

import service.BedService;
import ui.theme.AppTheme;

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

    private BedService bedService;

    private int selectedBedId = -1;
    private ui.components.SearchableTablePanel searchableTable;
    private boolean loadingRooms;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public BedPanel() {

        bedService = new BedService();

        setTitle("Hospital Management System - Bed Management");

        setSize(950, 650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();
        AppTheme.styleLegacyWindow(getContentPane());

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

        mainPanel.add(titleLabel, BorderLayout.NORTH);


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

        searchableTable = ui.components.SearchableTablePanel.wrap(bedTable, scrollPane,
                        "Search by room number, bed number, or status", () -> {
                            loadRooms();
                            loadBeds();
                        }, 2, 3, 4);
        tablePanel.add(searchableTable, BorderLayout.CENTER);


        mainPanel.add(
                tablePanel,
                BorderLayout.CENTER
        );
        setMinimumSize(new Dimension(780, 540));
        scrollPane.setPreferredSize(new Dimension(0, 300));
        bedTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] widths = {75, 75, 180, 180, 140};
        for (int column = 0; column < widths.length; column++) {
            bedTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }


        // =================================================
        // BUTTON EVENTS
        // =================================================

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

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
                                boolean selected = bedTable.getSelectedRow() >= 0;
                                updateButton.setEnabled(selected);
                                deleteButton.setEnabled(selected);
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
        if (loadingRooms) return;
        loadingRooms = true;
        roomComboBox.setEnabled(false);
        new SwingWorker<List<Object[]>, Void>() {
            @Override protected List<Object[]> doInBackground() {
                return bedService.getAllRooms();
            }

            @Override protected void done() {
                loadingRooms = false;
                roomComboBox.setEnabled(true);
                try {
                    roomComboBox.removeAllItems();
                    for (Object[] room : get()) {
                        roomComboBox.addItem(new RoomItem(Integer.parseInt(room[0].toString()),
                                room[1].toString(), room[2].toString()));
                    }
                } catch (Exception exception) {
                    searchableTable.setError("Could not load rooms. Check the database connection and retry.");
                }
            }
        }.execute();
    }


    // =====================================================
    // LOAD BEDS
    // =====================================================

    private void loadBeds() {
        searchableTable.loadAsync("Loading bed records…",
                "Could not load beds. Check the database connection and retry.",
                bedService::getAllBeds, beds -> {
                    tableModel.setRowCount(0);
                    for (Object[] bed : beds) tableModel.addRow(bed);
                });
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


        int roomId = selectedRoom.roomId;
        runBedMutation("Saving bed…", "Bed added successfully.", "Unable to add bed.",
                () -> bedService.addBed(roomId, bedNumber));
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


        int bedId = selectedBedId;
        int roomId = selectedRoom.roomId;
        runBedMutation("Updating bed…", "Bed updated successfully.", "Unable to update bed.",
                () -> bedService.updateBed(bedId, roomId, bedNumber, status));
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


        int bedId = selectedBedId;
        runBedMutation("Deleting bed…", "Bed deleted successfully.", "Unable to delete bed.",
                () -> bedService.deleteBed(bedId));
    }

    private void runBedMutation(String progress, String successMessage, String failureMessage,
                                java.util.concurrent.Callable<Boolean> operation) {
        searchableTable.setLoading(progress);
        ui.components.SwingTaskRunner.run(getContentPane(), operation, success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, successMessage, "Bed Management", JOptionPane.INFORMATION_MESSAGE);
                searchableTable.setLoaded();
                clearFields();
                loadBeds();
            } else {
                searchableTable.setError(failureMessage);
                JOptionPane.showMessageDialog(this, failureMessage, "Bed Management", JOptionPane.ERROR_MESSAGE);
            }
        }, exception -> {
            String message = "Unable to complete the bed operation. Check the database connection and retry.";
            searchableTable.setError(message);
            JOptionPane.showMessageDialog(this, message, "Bed Management", JOptionPane.ERROR_MESSAGE);
        });
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
