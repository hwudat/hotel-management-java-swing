package View;

import DAO.ServiceDAO;
import models.Service;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class ServiceUsageForm extends JFrame {

    private JTextField txtRoomID, txtCustomerName, txtPrice, txtSearch;
    private JComboBox<Service> cbService; 
    private JSpinner spinQuantity;
    private JLabel lblTotalMoney;
    private JTable table;
    private DefaultTableModel tableModel;
    
    private ServiceDAO dao;
    private List<Service> serviceList;

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(230, 126, 34);

    public ServiceUsageForm() {
        dao = new ServiceDAO(); 
        initUI();
        loadServicesToCombo();
    }

    private void initUI() {
        setTitle("Ghi Nhận Dịch Vụ Khách Hàng");
        setSize(1150, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("DỊCH VỤ PHÒNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("<html><font color='white'>Tìm theo Số phòng:</font></html>");
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tra cứu");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.add(createTablePanel());
        mainPanel.add(createFormPanel());
        add(mainPanel, BorderLayout.CENTER);

        ActionListener searchAction = e -> loadHistory(txtSearch.getText().trim());
        btnSearch.addActionListener(searchAction);
        txtSearch.addActionListener(searchAction);
    }

    private void loadServicesToCombo() {
        serviceList = dao.getAllServices();
        cbService.removeAllItems();
        for (Service s : serviceList) {
            cbService.addItem(s); 
        }
    }

    private void loadHistory(String roomId) {
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số phòng!");
            return;
        }
        
        dao.loadServiceUsageHistory(tableModel, roomId);
        
        txtRoomID.setText(roomId);
        String cusName = dao.getCustomerNameByRoom(roomId);
        txtCustomerName.setText(cusName);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lịch sử sử dụng của phòng"));

        String[] cols = {"Phòng", "Dịch Vụ", "SL", "Đơn Giá", "Thành Tiền", "Thời gian"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thêm dịch vụ mới"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addInputRow(inputPanel, "Số Phòng:", txtRoomID = new JTextField(), row++, gbc);
        txtRoomID.setEditable(false); 

        addInputRow(inputPanel, "Khách Hàng:", txtCustomerName = new JTextField(), row++, gbc);
        txtCustomerName.setEditable(false);

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Chọn Dịch Vụ:"), gbc);
        gbc.gridx = 1;
        cbService = new JComboBox<>(); 
        inputPanel.add(cbService, gbc);
        row++;

        addInputRow(inputPanel, "Đơn Giá:", txtPrice = new JTextField(), row++, gbc);
        txtPrice.setEditable(false);

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Số Lượng:"), gbc);
        gbc.gridx = 1;
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(spinQuantity, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("TỔNG TIỀN:"), gbc);
        gbc.gridx = 1;
        lblTotalMoney = new JLabel("0 VND");
        lblTotalMoney.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalMoney.setForeground(Color.RED);
        inputPanel.add(lblTotalMoney, gbc);

        ActionListener updatePrice = e -> {
            Service selected = (Service) cbService.getSelectedItem();
            if (selected != null) {
                txtPrice.setText(String.format("%,.0f", selected.getPrice()));
                calculateTotal();
            }
        };
        cbService.addActionListener(updatePrice);
        spinQuantity.addChangeListener(e -> calculateTotal());

        gbc.gridy = ++row; gbc.weighty = 1.0;
        inputPanel.add(new JLabel(), gbc);
        panel.add(inputPanel, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Xác nhận thêm");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(100, 40));
        
        btnAdd.addActionListener(e -> addServiceUsage());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void calculateTotal() {
        Service selected = (Service) cbService.getSelectedItem();
        if (selected != null) {
            int qty = (int) spinQuantity.getValue();
            double total = selected.getPrice() * qty;
            DecimalFormat df = new DecimalFormat("#,###");
            lblTotalMoney.setText(df.format(total) + " VND");
        }
    }

    private void addServiceUsage() {
        String roomId = txtRoomID.getText();
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn phòng! Hãy nhập số phòng ở ô tìm kiếm trước.");
            return;
        }
        
        Service selected = (Service) cbService.getSelectedItem();
        int qty = (int) spinQuantity.getValue();
        double total = selected.getPrice() * qty;

        if (dao.addServiceUsage(roomId, selected.getId(), qty, total)) {
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
            loadHistory(roomId); 
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm dịch vụ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addInputRow(JPanel p, String label, Component c, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weighty = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(c, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServiceUsageForm().setVisible(true));
    }
}