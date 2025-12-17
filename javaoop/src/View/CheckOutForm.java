package View;

import DAO.BookingDAO;
import DAO.RoomDAO;
import Utils.DatabaseConnection; 
import models.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class CheckOutForm extends JFrame {

    private JComboBox<String> cbRoomID;
    private JLabel lblCustomerName, lblPhone, lblCheckIn, lblRoomPrice;
    private JTable tblBillDetails;
    private DefaultTableModel tableModel;
    private JLabel lblTotalAmount;
    private JButton btnPay;

    // DAO
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);

    public CheckOutForm() {
        roomDAO = new RoomDAO();
        bookingDAO = new BookingDAO();
        initUI();
        loadOccupiedRooms(); 
    }

    private void initUI() {
        setTitle("Check-Out & Thanh Toán");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("TRẢ PHÒNG & XUẤT HÓA ĐƠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 20));
        leftPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Chọn phòng cần trả"));
        
        searchPanel.add(new JLabel("Số phòng: "));
        cbRoomID = new JComboBox<>();
        cbRoomID.setPreferredSize(new Dimension(150, 30));
        searchPanel.add(cbRoomID);

        JButton btnFind = new JButton("Lấy thông tin");
        btnFind.setBackground(PRIMARY_COLOR);
        btnFind.setForeground(Color.WHITE);
        btnFind.addActionListener(e -> loadBookingInfo((String) cbRoomID.getSelectedItem())); 
        searchPanel.add(btnFind);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lưu trú"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int row = 0;
        lblCustomerName = addInfoRow(infoPanel, "Khách hàng:", "---", row++, gbc);
        lblPhone = addInfoRow(infoPanel, "SĐT:", "---", row++, gbc);
        lblRoomPrice = addInfoRow(infoPanel, "Đơn giá phòng:", "---", row++, gbc);
        lblCheckIn = addInfoRow(infoPanel, "Ngày Check-in:", "---", row++, gbc);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));

        String[] columns = {"Hạng mục", "Chi tiết", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0);
        tblBillDetails = new JTable(tableModel);
        tblBillDetails.setRowHeight(25);
        rightPanel.add(new JScrollPane(tblBillDetails), BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        lblTotalAmount = new JLabel("0 VNĐ");
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalAmount.setForeground(Color.RED);
        totalPanel.add(new JLabel("TỔNG THANH TOÁN: "));
        totalPanel.add(lblTotalAmount);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPay = new JButton("XÁC NHẬN THANH TOÁN");
        btnPay.setBackground(ACCENT_COLOR);
        btnPay.setForeground(Color.WHITE);
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPay.setPreferredSize(new Dimension(220, 40));
        
        btnPay.addActionListener(e -> performCheckOut());

        btnPanel.add(btnPay);
        footerPanel.add(totalPanel, BorderLayout.NORTH);
        footerPanel.add(btnPanel, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel addInfoRow(JPanel p, String title, String value, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lblTitle, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(lblValue, gbc);
        return lblValue;
    }

    private void loadOccupiedRooms() {
        cbRoomID.removeAllItems();
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room r : rooms) {
            
            if (r.getStatus().equalsIgnoreCase("Occupied") || r.getStatus().equalsIgnoreCase("Đang ở")) {
                cbRoomID.addItem(r.getRoomID());
            }
        }
    }

    // Bạn nhớ thêm import DAO.ServiceDAO; ở trên cùng file nhé

    private void loadBookingInfo(String roomId) {
        if (roomId == null) return;

        // [SỬA 1] Thêm b.booking_id vào câu lệnh SELECT để lấy ID đơn đặt
        String sql = "SELECT b.booking_id, c.full_name, c.phone, b.check_in_date, rt.price_per_night " +
                "FROM Booking b " +
                "JOIN Customer c ON b.customer_id = c.customer_id " +
                "JOIN Room r ON b.room_id = r.room_id " +
                "JOIN RoomType rt ON r.type_id = rt.type_id " +
                "WHERE b.room_id = ? AND b.status = 'Checked In'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 1. Lấy thông tin cơ bản
                int bookingId = rs.getInt("booking_id");
                lblCustomerName.setText(rs.getString("full_name"));
                lblPhone.setText(rs.getString("phone"));

                java.sql.Timestamp checkIn = rs.getTimestamp("check_in_date");
                lblCheckIn.setText(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(checkIn));

                double pricePerNight = rs.getDouble("price_per_night");
                lblRoomPrice.setText(String.format("%,.0f VNĐ", pricePerNight));

                // 2. Tính toán tiền phòng (Check-in đến Hiện tại)
                long currentTime = System.currentTimeMillis();
                long diff = currentTime - checkIn.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                if (days == 0) days = 1; // Nếu ở chưa được 1 ngày thì tính là 1 ngày

                double totalRoomPrice = days * pricePerNight;

                // Reset bảng và thêm dòng Tiền phòng
                tableModel.setRowCount(0);
                tableModel.addRow(new Object[]{
                        "Tiền phòng",
                        days + " ngày",
                        String.format("%,.0f", totalRoomPrice)
                });

                // Biến tổng tiền tích lũy
                double finalTotal = totalRoomPrice;

                // 3. [MỚI] Lấy danh sách Dịch vụ từ DAO và cộng vào
                DAO.ServiceDAO serviceDAO = new DAO.ServiceDAO();
                java.util.List<Object[]> services = serviceDAO.getServicesByBookingId(bookingId);

                if (services != null) {
                    for (Object[] sv : services) {
                        String svName = (String) sv[0];
                        int qty = (int) sv[1];
                        double svTotal = (double) sv[2];

                        // Thêm vào bảng
                        tableModel.addRow(new Object[]{
                                "Dịch vụ: " + svName,
                                "SL: " + qty,
                                String.format("%,.0f", svTotal)
                        });

                        // Cộng dồn tiền
                        finalTotal += svTotal;
                    }
                }

                // 4. Hiển thị Tổng tiền cuối cùng
                lblTotalAmount.setText(String.format("%,.0f VNĐ", finalTotal));

            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt phòng!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performCheckOut() {
        String roomId = (String) cbRoomID.getSelectedItem();
        if (roomId == null) return;

        int choice = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán và trả phòng " + roomId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = bookingDAO.checkOut(roomId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Trả phòng THÀNH CÔNG!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi trả phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}