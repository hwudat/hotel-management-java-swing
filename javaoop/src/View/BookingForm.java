package View;

import DAO.BookingDAO;
import DAO.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

public class BookingForm extends JFrame {

    // Components
    private JSpinner spinCheckIn, spinCheckOut;
    private JComboBox<String> cbRoomTypeFilter;
    private JTable tblAvailableRooms;
    private DefaultTableModel tableModelRooms;

    // Form Khách hàng
    private JTextField txtRoomSelected;
    private JTextField txtCustomerName, txtPhone, txtCCCD, txtDeposit, txtAddress;
    private JTextArea txtNote;
    private JRadioButton radMale, radFemale;
    private ButtonGroup bgGender;

    // DAO
    private BookingDAO bookingDAO;
    private CustomerDAO customerDAO;

    public BookingForm() {
        bookingDAO = new BookingDAO();
        customerDAO = new CustomerDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Đặt Phòng / Check-in Mới");
        setSize(1200, 700); // Tăng kích thước để chứa thêm form
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header tìm kiếm
        add(createSearchPanel(), BorderLayout.NORTH);

        // Main content chia 2 cột: Danh sách phòng - Thông tin khách
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainContent.add(createAvailableRoomsPanel());
        mainContent.add(createBookingInfoPanel());
        add(mainContent, BorderLayout.CENTER);

        // Footer chứa nút bấm
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnSave = new JButton(" Lưu & Check-In");
        btnSave.setBackground(new Color(46, 204, 113)); // Xanh lá
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(180, 40));
        btnSave.addActionListener(e -> handleSaveBooking());

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());

        footerPanel.add(btnCancel);
        footerPanel.add(btnSave);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(240, 245, 250));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        panel.add(new JLabel("Ngày vào:"));
        spinCheckIn = createDateSpinner();
        panel.add(spinCheckIn);

        panel.add(new JLabel("Ngày ra:"));
        spinCheckOut = createDateSpinner();
        panel.add(spinCheckOut);

        panel.add(new JLabel("Loại phòng:"));
        cbRoomTypeFilter = new JComboBox<>(new String[]{"Tất cả", "Standard", "Deluxe", "VIP"});
        panel.add(cbRoomTypeFilter);

        JButton btnSearch = new JButton("🔍 Tìm phòng trống");
        btnSearch.setBackground(new Color(52, 152, 219));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> loadAvailableRooms());
        panel.add(btnSearch);

        return panel;
    }

    private JPanel createAvailableRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Kết quả: Phòng trống"));

        String[] cols = {"Số phòng", "Loại", "Giá (VND)", "Mô tả"};
        tableModelRooms = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblAvailableRooms = new JTable(tableModelRooms);
        tblAvailableRooms.setRowHeight(25);
        tblAvailableRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblAvailableRooms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblAvailableRooms.getSelectedRow();
                if (row >= 0) {
                    String roomID = tableModelRooms.getValueAt(row, 0).toString();
                    txtRoomSelected.setText(roomID);
                }
            }
        });

        panel.add(new JScrollPane(tblAvailableRooms), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder("Thông tin khách hàng & Đặt phòng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Phòng chọn
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Phòng chọn:"), gbc);
        gbc.gridx = 1;
        txtRoomSelected = new JTextField();
        txtRoomSelected.setEditable(false);
        txtRoomSelected.setBackground(new Color(255, 250, 205)); // Màu vàng nhạt
        txtRoomSelected.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(txtRoomSelected, gbc);

        // Dòng 2: Số điện thoại (Quan trọng: Nhập xong tự tìm khách cũ)
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField();
        // Sự kiện: Khi nhập xong SĐT thì tự động tìm thông tin
        txtPhone.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkCustomer(); }
            public void removeUpdate(DocumentEvent e) { checkCustomer(); }
            public void changedUpdate(DocumentEvent e) { checkCustomer(); }

            // Logic tìm khách cũ
            void checkCustomer() {
                String phone = txtPhone.getText().trim();
                if (phone.length() >= 9) { // Chỉ tìm khi nhập đủ dài
                    Customer oldCust = customerDAO.getCustomerByPhone(phone); // Cần thêm hàm này trong DAO
                    if (oldCust != null) {
                        txtCustomerName.setText(oldCust.getFullName());
                        txtCCCD.setText(oldCust.getIdentityCard());
                        txtAddress.setText(oldCust.getAddress()); // Tự điền địa chỉ
                        if("Nam".equalsIgnoreCase(oldCust.getGender())) radMale.setSelected(true);
                        else radFemale.setSelected(true);
                    }
                }
            }
        });
        panel.add(txtPhone, gbc);

        // Dòng 3: Họ tên
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Họ tên khách (*):"), gbc);
        gbc.gridx = 1;
        txtCustomerName = new JTextField();
        panel.add(txtCustomerName, gbc);

        // Dòng 4: CCCD/CMND
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("CCCD/CMND:"), gbc);
        gbc.gridx = 1;
        txtCCCD = new JTextField();
        panel.add(txtCCCD, gbc);

        // Dòng 5: Giới tính
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radMale = new JRadioButton("Nam", true);
        radFemale = new JRadioButton("Nữ");
        bgGender = new ButtonGroup();
        bgGender.add(radMale); bgGender.add(radFemale);
        genderPanel.add(radMale); genderPanel.add(radFemale);
        panel.add(genderPanel, gbc);

        // Dòng 6: Địa chỉ (Mới thêm)
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField();
        panel.add(txtAddress, gbc);

        // Dòng 7: Tiền cọc
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Tiền đặt cọc (VND):"), gbc);
        gbc.gridx = 1;
        txtDeposit = new JTextField("0");
        panel.add(txtDeposit, gbc);

        // Dòng 8: Ghi chú
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        txtNote = new JTextArea(3, 20);
        txtNote.setLineWrap(true);
        panel.add(new JScrollPane(txtNote), gbc);

        gbc.gridy = 8; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);

        return panel;
    }

    private void loadAvailableRooms() {
        String type = (String) cbRoomTypeFilter.getSelectedItem();
        bookingDAO.searchAvailableRooms(tableModelRooms, type);

        if (tableModelRooms.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng trống nào phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleSaveBooking() {
        // 1. Validate dữ liệu nhập
        String roomID = txtRoomSelected.getText();
        String name = txtCustomerName.getText().trim();
        String phone = txtPhone.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String address = txtAddress.getText().trim();
        String gender = radMale.isSelected() ? "Nam" : "Nữ";

        if (roomID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng từ danh sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và SĐT khách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- [THÊM MỚI] KIỂM TRA NGÀY THÁNG ---
        Date checkIn = (Date) spinCheckIn.getValue();
        Date checkOut = (Date) spinCheckOut.getValue();

        // Kiểm tra nếu Ngày ra <= Ngày vào -> Báo lỗi ngay
        if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
            JOptionPane.showMessageDialog(this,
                    "Ngày Check-out phải sau ngày Check-in ít nhất 1 phút!",
                    "Lỗi Ngày Tháng",
                    JOptionPane.WARNING_MESSAGE);
            return; // Dừng lại, không gửi xuống Database
        }
        // ----------------------------------------

        try {
            // 2. Xử lý khách hàng
            int customerId = bookingDAO.findOrCreateCustomerFull(name, phone, cccd, gender, address);

            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin khách hàng vào CSDL!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Tạo Booking
            double deposit = 0;
            if (!txtDeposit.getText().isEmpty()) {
                try {
                    deposit = Double.parseDouble(txtDeposit.getText().replace(",", ""));
                } catch (Exception ex) {}
            }

            // Gọi hàm thêm Booking
            boolean success = bookingDAO.addBooking(customerId, roomID, checkIn, checkOut, deposit);

            if (success) {
                String msg = "Check-in THÀNH CÔNG!\nPhòng: " + roomID + "\nKhách: " + name;
                JOptionPane.showMessageDialog(this, msg);
                this.dispose(); // Đóng form
            } else {
                // Nếu vẫn lỗi, khả năng cao là do tên cột trong BookingDAO chưa khớp với Database
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo đơn đặt phòng!\n(Vui lòng kiểm tra lại tên cột trong BookingDAO)", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }
    // Helpers UI
    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm");
        spinner.setEditor(editor);
        spinner.setValue(new Date());
        return spinner;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(44, 62, 80)
        );
    }
}