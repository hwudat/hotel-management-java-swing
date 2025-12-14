package View;

import DAO.CustomerDAO;
import models.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerManagementForm extends JFrame {

    // Components
    private JTextField txtCustomerID, txtRoomNumber, txtName, txtCCCD, txtPhone, txtNationality;
    private JRadioButton radMale, radFemale;
    private ButtonGroup genderGroup;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    // DAO
    private CustomerDAO customerDAO;

    // Màu sắc
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);

    public CustomerManagementForm() {
        customerDAO = new CustomerDAO(); 
        loadData(""); 
    }

    private void initUI() {
        setTitle("Quản Lý Hồ Sơ Khách Hàng");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("DANH SÁCH KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);

        JLabel lblSearch = new JLabel("<html><font color='white'>Tìm Tên/SĐT/CCCD:</font></html>");
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tìm kiếm");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFormPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadData(String keyword) {
        customerDAO.loadCustomerData(tableModel, keyword);
    }

    private void addCustomer() {
        if (!validateInput()) return;
        
        Customer c = new Customer();
        c.setFullName(txtName.getText());
        c.setIdentityCard(txtCCCD.getText());
        c.setGender(radMale.isSelected() ? "Nam" : "Nữ");
        c.setPhone(txtPhone.getText());
        c.setNationality(txtNationality.getText());

        if (customerDAO.addCustomer(c)) {
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData("");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi! Có thể trùng SĐT hoặc CCCD.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (txtCustomerID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
            return;
        }
        if (!validateInput()) return;

        Customer c = new Customer();
        c.setCustomerId(Integer.parseInt(txtCustomerID.getText()));
        c.setFullName(txtName.getText());
        c.setIdentityCard(txtCCCD.getText());
        c.setGender(radMale.isSelected() ? "Nam" : "Nữ");
        c.setPhone(txtPhone.getText());
        c.setNationality(txtNationality.getText());

        if (customerDAO.updateCustomer(c)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadData("");
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (txtCustomerID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtCustomerID.getText());
            if (customerDAO.deleteCustomer(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa! Khách hàng này đang có lịch sử đặt phòng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        if (txtName.getText().isEmpty() || txtPhone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và Số điện thoại!");
            return false;
        }
        return true;
    }


    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Danh sách khách hàng lưu trữ",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));

        String[] cols = {"Mã KH", "Phòng", "Họ và Tên", "CCCD/CMND", "Giới tính", "SĐT", "Quốc tịch"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) fillFormFromTable(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fillFormFromTable(int row) {
        txtCustomerID.setText(tableModel.getValueAt(row, 0).toString());
        txtRoomNumber.setText(tableModel.getValueAt(row, 1).toString());
        txtName.setText(tableModel.getValueAt(row, 2).toString());
        txtCCCD.setText(tableModel.getValueAt(row, 3).toString());
        
        String gender = tableModel.getValueAt(row, 4).toString();
        if(gender.equalsIgnoreCase("Nam")) radMale.setSelected(true);
        else radFemale.setSelected(true);

        txtPhone.setText(tableModel.getValueAt(row, 5).toString());
        txtNationality.setText(tableModel.getValueAt(row, 6).toString());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR), "Thông tin chi tiết",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addInputRow(inputPanel, "Mã khách hàng:", txtCustomerID = new JTextField(), row++, gbc);
        txtCustomerID.setEditable(false);
        txtCustomerID.setBackground(new Color(240, 240, 240));

        addInputRow(inputPanel, "Phòng đang thuê:", txtRoomNumber = new JTextField(), row++, gbc);
        txtRoomNumber.setEditable(false); 
        txtRoomNumber.setFont(new Font("Segoe UI", Font.BOLD, 12));

        addInputRow(inputPanel, "Họ và Tên:", txtName = new JTextField(), row++, gbc);
        addInputRow(inputPanel, "CCCD/CMND:", txtCCCD = new JTextField(), row++, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radMale = new JRadioButton("Nam");
        radFemale = new JRadioButton("Nữ");
        genderGroup = new ButtonGroup();
        genderGroup.add(radMale); genderGroup.add(radFemale);
        radMale.setSelected(true);
        genderPanel.add(radMale); genderPanel.add(radFemale);
        inputPanel.add(genderPanel, gbc);
        row++;

        addInputRow(inputPanel, "Số điện thoại:", txtPhone = new JTextField(), row++, gbc);
        addInputRow(inputPanel, "Quốc tịch:", txtNationality = new JTextField(), row++, gbc);

        gbc.gridy = row; gbc.weighty = 1.0;
        inputPanel.add(new JLabel(), gbc);
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAdd = createButton("Thêm Khách", new Color(46, 204, 113));
        JButton btnEdit = createButton("Cập nhật", new Color(241, 196, 15));
        JButton btnDelete = createButton("Xóa", new Color(231, 76, 60));
        JButton btnClear = createButton("Làm mới", Color.GRAY);

        btnAdd.addActionListener(e -> addCustomer());
        btnEdit.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
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

    private void clearForm() {
        txtCustomerID.setText("");
        txtRoomNumber.setText("");
        txtName.setText("");
        txtCCCD.setText("");
        txtPhone.setText("");
        txtNationality.setText("");
        radMale.setSelected(true);
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerManagementForm().setVisible(true));
    }
}