// File: src/HotelSystemGUI.java
import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HotelSystemGUI extends JFrame {
    private final ArrayList<Room> rooms;
    private final ArrayList<Reservation> reservations;
    private JTable roomTable;
    private JTable bookingTable;
    private DefaultTableModel roomTableModel;
    private DefaultTableModel bookingTableModel;

    public HotelSystemGUI() {
        // --- 1. Modern Look and Feel ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize Look and Feel.");
        }

        setTitle("Grand Hotel - Reservation System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(createIcon("/resources/hotel-icon.png").getImage());

        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        initializeRooms();
        initComponents();
        layoutComponents();
        updateTables();
    }

    private ImageIcon createIcon(String path) {
        URL imageURL = getClass().getResource(path);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Resource not found: " + path);
            return null;
        }
    }

    private void initializeRooms() {
        rooms.add(new Room(101, "Standard", 50.0));
        rooms.add(new Room(102, "Standard", 50.0));
        rooms.add(new Room(201, "Deluxe", 80.0));
        rooms.add(new Room(202, "Deluxe", 80.0));
        rooms.add(new Room(301, "Suite", 150.0));
    }

    private void initComponents() {
        // --- Room Table with Custom Renderer ---
        roomTableModel = new DefaultTableModel(new String[]{"Room No.", "Type", "Price/Night", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable = new JTable(roomTableModel);
        roomTable.setRowHeight(35);
        roomTable.getTableHeader().setFont(roomTable.getFont().deriveFont(Font.BOLD));
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setShowHorizontalLines(false);
        roomTable.setShowVerticalLines(false);
        roomTable.setIntercellSpacing(new Dimension(0, 5));
        roomTable.setDefaultRenderer(Object.class, new RoomStatusCellRenderer());

        // --- Booking Table ---
        bookingTableModel = new DefaultTableModel(new String[]{"Guest", "Room No.", "Check-In", "Check-Out"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.setRowHeight(30);
        bookingTable.getTableHeader().setFont(bookingTable.getFont().deriveFont(Font.BOLD));
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setShowHorizontalLines(false);
        bookingTable.setShowVerticalLines(false);
        bookingTable.setIntercellSpacing(new Dimension(0, 5));
    }

    private void layoutComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // --- Availability Tab ---
        JPanel availabilityPanel = createAvailabilityPanel();
        tabbedPane.addTab("Room Availability", availabilityPanel);

        // --- Bookings Tab ---
        JPanel bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("Current Bookings", bookingsPanel);

        add(tabbedPane);
    }

    private JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JButton bookButton = new JButton("Book Selected Room");
        bookButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        bookButton.addActionListener(e -> bookSelectedRoom());
        panel.add(bookButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JButton cancelButton = new JButton("Cancel Selected Booking");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.addActionListener(e -> cancelSelectedBooking());
        panel.add(cancelButton, BorderLayout.SOUTH);
        return panel;
    }

    // Custom cell renderer to show icons in the status column
    class RoomStatusCellRenderer extends DefaultTableCellRenderer {
        private final JLabel availableLabel = new JLabel("Available", createIcon("/resources/available.png"), JLabel.LEFT);
        private final JLabel bookedLabel = new JLabel("Booked", createIcon("/resources/booked.png"), JLabel.LEFT);

        public RoomStatusCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == 3) { // Status column
                Room room = rooms.get(row);
                JLabel label = room.isAvailable() ? availableLabel : bookedLabel;
                label.setOpaque(isSelected);
                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private void updateTables() {
        updateRoomTable();
        updateBookingTable();
    }

    private void updateRoomTable() {
        roomTableModel.setRowCount(0);
        for (Room room : rooms) {
            Object[] row = {room.getRoomNumber(), room.getType(), String.format("$%.2f", room.getPricePerNight()), room.isAvailable() ? "Available" : "Booked"};
            roomTableModel.addRow(row);
        }
    }

    private void updateBookingTable() {
        bookingTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (Reservation res : reservations) {
            Object[] row = {res.getGuestName(), res.getRoom().getRoomNumber(), res.getCheckInDate().format(formatter), res.getCheckOutDate().format(formatter)};
            bookingTableModel.addRow(row);
        }
    }

    private void bookSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to book.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Room selectedRoom = rooms.get(selectedRow);
        if (!selectedRoom.isAvailable()) {
            JOptionPane.showMessageDialog(this, "This room is already booked.", "Room Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // The booking dialog is the same as before, but you could make it more complex
        // For now, we'll use a simple JOptionPane for input
        JTextField nameField = new JTextField();
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Guest Name:")); panel.add(nameField);
        panel.add(new JLabel("Check-In (YYYY-MM-DD):")); panel.add(checkInField);
        panel.add(new JLabel("Check-Out (YYYY-MM-DD):")); panel.add(checkOutField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Book Room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String guestName = nameField.getText().trim();
                LocalDate checkIn = LocalDate.parse(checkInField.getText().trim());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim());
                if (guestName.isEmpty() || checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) throw new IllegalArgumentException("Invalid booking details.");
                selectedRoom.setAvailable(false);
                reservations.add(new Reservation(guestName, selectedRoom, checkIn, checkOut));
                updateTables();
                JOptionPane.showMessageDialog(this, "Room booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (HeadlessException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Reservation selectedReservation = reservations.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel booking for " + selectedReservation.getGuestName() + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            selectedReservation.getRoom().setAvailable(true);
            reservations.remove(selectedReservation);
            updateTables();
            JOptionPane.showMessageDialog(this, "Booking cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelSystemGUI().setVisible(true));
    }
}