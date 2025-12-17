package View;

import DAO.EmployeeDAO; // [MỚI] Import DAO
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

public class EmployeeManagerForm extends JFrame {

    // Components
    private JTextField txtEmpID, txtName, txtPhone, txtEmail, txtSalary, txtSearch;
    private JComboBox<String> cbPosition, cbGender;
    private JTable table;
    private DefaultTableModel tableModel;

    // [MỚI] Khai báo DAO
    private EmployeeDAO employeeDAO;

    // Dữ liệu mẫu cho Chức vụ
    private final String[] POSITIONS = {"Quản Lý", "Lễ Tân", "Buồng Phòng", "Bảo Vệ", "Kế Toán", "Bếp Trưởng"};

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);

    public EmployeeManagerForm() {
        employeeDAO = new EmployeeDAO(); // [MỚI] Khởi tạo DAO
        initUI();
        loadDataFromDB(""); // [MỚI] Load từ SQL thay vì MockData
    }

    private void initUI() {
        setTitle("Quản Lý Hồ Sơ Nhân Viên");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("DANH SÁCH NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        // Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tìm kiếm");

        // Sự kiện tìm kiếm
        btnSearch.addActionListener(e -> loadDataFromDB(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadDataFromDB(txtSearch.getText().trim()));

        searchPanel.add(new JLabel("<html><font color='white'>Tìm tên/Mã NV:</font></html>"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. MAIN CONTENT ---
        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFormPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    // [MỚI] Hàm gọi DAO để load dữ liệu
    private void loadDataFromDB(String keyword) {
        employeeDAO.loadData(tableModel, keyword);
    }

    // Tạo bảng danh sách
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách nhân sự",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));

        String[] cols = {"Mã NV", "Họ Tên", "Chức Vụ", "Giới Tính", "SĐT", "Email", "Lương CB"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Click bảng -> Đổ dữ liệu
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    fillForm(row);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // Tạo Form nhập liệu
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR), "Thông tin nhân viên",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addInputRow(inputPanel, "Mã nhân viên:", txtEmpID = new JTextField(), row++, gbc);
        txtEmpID.setEditable(false);
        txtEmpID.setText("Tự động");
        txtEmpID.setBackground(new Color(240, 240, 240));

        addInputRow(inputPanel, "Họ và Tên:", txtName = new JTextField(), row++, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 1;
        cbPosition = new JComboBox<>(POSITIONS);
        inputPanel.add(cbPosition, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        inputPanel.add(cbGender, gbc);
        row++;

        addInputRow(inputPanel, "Số điện thoại:", txtPhone = new JTextField(), row++, gbc);
        addInputRow(inputPanel, "Email:", txtEmail = new JTextField(), row++, gbc);
        addInputRow(inputPanel, "Lương cơ bản (VND):", txtSalary = new JTextField(), row++, gbc);

        gbc.gridy = row; gbc.weighty = 1.0;
        inputPanel.add(new JLabel(), gbc);
        panel.add(inputPanel, BorderLayout.CENTER);

        // Nút bấm
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAdd = createButton("Thêm NV", new Color(46, 204, 113));
        JButton btnEdit = createButton("Cập nhật", new Color(241, 196, 15));
        JButton btnDel = createButton("Sa thải / Xóa", new Color(231, 76, 60));
        JButton btnClear = createButton("Làm mới", Color.GRAY);

        // --- XỬ LÝ SỰ KIỆN GỌI DAO ---

        // 1. THÊM
        btnAdd.addActionListener(e -> {
            if(txtName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhân viên!");
                return;
            }
            double salary = parseSalary(txtSalary.getText());

            if(employeeDAO.addEmployee(
                    txtName.getText(),
                    cbPosition.getSelectedItem().toString(),
                    cbGender.getSelectedItem().toString(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    salary
            )) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                loadDataFromDB("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm! Có thể tên đăng nhập bị trùng.");
            }
        });

        // 2. SỬA
        btnEdit.addActionListener(e -> {
            if(txtEmpID.getText().equals("Tự động")) {
                JOptionPane.showMessageDialog(this, "Chọn nhân viên cần sửa trước!");
                return;
            }
            int id = Integer.parseInt(txtEmpID.getText().replace("NV", ""));
            double salary = parseSalary(txtSalary.getText());

            if(employeeDAO.updateEmployee(
                    id,
                    txtName.getText(),
                    cbPosition.getSelectedItem().toString(),
                    cbGender.getSelectedItem().toString(),
                    txtPhone.getText(),
                    txtEmail.getText(),
                    salary
            )) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataFromDB("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật!");
            }
        });

        // 3. XÓA
        btnDel.addActionListener(e -> {
            if(txtEmpID.getText().equals("Tự động")) return;

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nhân viên này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(txtEmpID.getText().replace("NV", ""));
                if(employeeDAO.deleteEmployee(id)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa nhân viên!");
                    loadDataFromDB("");
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa! Nhân viên này đang có dữ liệu trong hệ thống (Hóa đơn/Booking).");
                }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void fillForm(int row) {
        txtEmpID.setText(tableModel.getValueAt(row, 0).toString());
        txtName.setText(tableModel.getValueAt(row, 1).toString());

        String pos = tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "";
        cbPosition.setSelectedItem(pos);

        String gender = tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "Nam";
        cbGender.setSelectedItem(gender);

        txtPhone.setText(tableModel.getValueAt(row, 4).toString());
        txtEmail.setText(tableModel.getValueAt(row, 5).toString());
        txtSalary.setText(tableModel.getValueAt(row, 6).toString().replace(",", ""));
    }

    private void clearForm() {
        txtEmpID.setText("Tự động");
        txtName.setText("");
        cbPosition.setSelectedIndex(0);
        cbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtEmail.setText("");
        txtSalary.setText("");
        table.clearSelection();
    }

    private double parseSalary(String input) {
        try {
            return Double.parseDouble(input.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void addInputRow(JPanel p, String label, JTextField tf, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weighty = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(tf, gbc);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EmployeeManagerForm().setVisible(true));
    }
}