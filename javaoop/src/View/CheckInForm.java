package View;

import DAO.BookingDAO;
import DAO.RoomDAO;
import models.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class CheckInForm extends JFrame {

    private JTextField txtCustomerID, txtName, txtPhone; 
    private JComboBox<String> cbRoomNumber;
    private JSpinner spinCheckIn;
    private JCheckBox chkBreakfast, chkLaundry, chkSpa;
    private JButton btnCheckIn, btnCancel;
    
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;

    public CheckInForm() {
        roomDAO = new RoomDAO();
        bookingDAO = new BookingDAO();
        
        setTitle("Quản Lý Khách Sạn - Check In");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("CHECK-IN KHÁCH HÀNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addItem(leftPanel, new JLabel("ID Khách hàng (*):"), 0, 0, gbc);
        txtCustomerID = new JTextField(20);
       
        txtCustomerID.setToolTipText("Nhập ID khách đã có trong hệ thống (VD: 1)"); 
        addItem(leftPanel, txtCustomerID, 1, 0, gbc);

        addItem(leftPanel, new JLabel("Họ và Tên:"), 0, 1, gbc);
        txtName = new JTextField(20);
        addItem(leftPanel, txtName, 1, 1, gbc);

        addItem(leftPanel, new JLabel("Số điện thoại:"), 0, 2, gbc);
        txtPhone = new JTextField(20);
        addItem(leftPanel, txtPhone, 1, 2, gbc);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng & Dịch vụ"));

        addItem(rightPanel, new JLabel("Chọn Phòng Trống:"), 0, 0, gbc);
        cbRoomNumber = new JComboBox<>();
        loadAvailableRooms(); 
        addItem(rightPanel, cbRoomNumber, 1, 0, gbc);

        addItem(rightPanel, new JLabel("Ngày Check-in:"), 0, 1, gbc);
        spinCheckIn = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditorIn = new JSpinner.DateEditor(spinCheckIn, "dd/MM/yyyy HH:mm");
        spinCheckIn.setEditor(timeEditorIn);
        spinCheckIn.setValue(new Date());
        spinCheckIn.setEnabled(false); 
        addItem(rightPanel, spinCheckIn, 1, 1, gbc);

        addItem(rightPanel, new JLabel("Dịch vụ thêm:"), 0, 2, gbc);
        JPanel servicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chkBreakfast = new JCheckBox("Ăn sáng");
        chkLaundry = new JCheckBox("Giặt ủi");
        chkSpa = new JCheckBox("Spa");
        servicePanel.add(chkBreakfast);
        servicePanel.add(chkLaundry);
        servicePanel.add(chkSpa);
        addItem(rightPanel, servicePanel, 1, 2, gbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        btnCheckIn = new JButton("Xác nhận Check-In");
        btnCheckIn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckIn.setBackground(new Color(46, 204, 113));
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckIn.setFocusPainted(false);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);

        buttonPanel.add(btnCheckIn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        btnCheckIn.addActionListener(e -> performCheckIn());
        btnCancel.addActionListener(e -> dispose());
    }

    private void addItem(JPanel p, JComponent c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = (x == 0) ? 0.3 : 0.7;
        p.add(c, gbc);
    }

    private void loadAvailableRooms() {
        cbRoomNumber.removeAllItems();
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room r : rooms) {
            
            if (r.getStatus().equalsIgnoreCase("Available") || r.getStatus().equalsIgnoreCase("Phòng trống")) {
                cbRoomNumber.addItem(r.getRoomID()); 
            }
        }
    }

    private void performCheckIn() {
        try {
            
            String cusIdText = txtCustomerID.getText();
            String roomID = (String) cbRoomNumber.getSelectedItem();

            if (cusIdText.isEmpty() || roomID == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID Khách hàng và chọn phòng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int customerId = Integer.parseInt(cusIdText);
            int employeeId = 1;

            boolean success = bookingDAO.checkIn(customerId, roomID, employeeId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Check-in THÀNH CÔNG!\nPhòng " + roomID + " đã chuyển sang trạng thái Đang ở.");
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Check-in THẤT BẠI!\nKiểm tra lại ID Khách hàng (phải tồn tại trong bảng Customer).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Khách hàng phải là số!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
}