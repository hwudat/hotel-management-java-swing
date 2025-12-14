package View;

import DAO.BookingDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

public class BookingForm extends JFrame {

    private JSpinner spinCheckIn, spinCheckOut;
    private JComboBox<String> cbRoomTypeFilter;
    private JTable tblAvailableRooms;
    private DefaultTableModel tableModelRooms;
    private JTextField txtCustomerName, txtPhone, txtCCCD, txtDeposit, txtRoomSelected;
    private JTextArea txtNote;

    private BookingDAO bookingDAO;

    public BookingForm() {
        bookingDAO = new BookingDAO(); 
        initUI();
    }

    private void initUI() {
        setTitle("Đặt Phòng / Check-in Mới");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSearchPanel(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainContent.add(createAvailableRoomsPanel());
        mainContent.add(createBookingInfoPanel());
        add(mainContent, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnSave = new JButton(" Lưu & Check-In");
        btnSave.setBackground(new Color(46, 204, 113));
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
        panel.setBorder(createTitledBorder("Thông tin người đặt"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Phòng chọn:"), gbc);
        gbc.gridx = 1;
        txtRoomSelected = new JTextField();
        txtRoomSelected.setEditable(false);
        txtRoomSelected.setBackground(new Color(255, 250, 205));
        txtRoomSelected.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(txtRoomSelected, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Họ tên khách (*):"), gbc);
        gbc.gridx = 1;
        txtCustomerName = new JTextField();
        panel.add(txtCustomerName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField();
        panel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("CCCD/CMND:"), gbc);
        gbc.gridx = 1;
        txtCCCD = new JTextField();
        panel.add(txtCCCD, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tiền đặt cọc (VND):"), gbc);
        gbc.gridx = 1;
        txtDeposit = new JTextField("0");
        panel.add(txtDeposit, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        txtNote = new JTextArea(3, 20);
        txtNote.setLineWrap(true);
        panel.add(new JScrollPane(txtNote), gbc);

        gbc.gridy = 6; gbc.weighty = 1.0;
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
        String roomID = txtRoomSelected.getText();
        String name = txtCustomerName.getText().trim();
        String phone = txtPhone.getText().trim();
        String cccd = txtCCCD.getText().trim();
        
        if (roomID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng từ danh sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và SĐT khách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = bookingDAO.findOrCreateCustomer(name, phone, cccd);
        if (customerId == -1) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thông tin khách hàng!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date checkIn = (Date) spinCheckIn.getValue();
        Date checkOut = (Date) spinCheckOut.getValue();
        double deposit = 0;
        try {
            deposit = Double.parseDouble(txtDeposit.getText().replace(",", ""));
        } catch (NumberFormatException e) {
            
        }

        boolean success = bookingDAO.addBooking(customerId, roomID, checkIn, checkOut, deposit);

        if (success) {
            String msg = "Đặt phòng THÀNH CÔNG!\n" +
                    "Phòng: " + roomID + "\n" +
                    "Khách: " + name + " (ID: " + customerId + ")";
            JOptionPane.showMessageDialog(this, msg);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu đặt phòng!", "Lỗi DB", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingForm().setVisible(true));
    }
}