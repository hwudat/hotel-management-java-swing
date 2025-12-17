package View;

import DAO.ServiceDAO;
import models.Service;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class ServiceManagerForm extends JFrame {

    private JTextField txtServiceID, txtServiceName, txtPrice, txtSearch;
    private JComboBox<String> cbCategory, cbUnit;
    private JTable table;
    private DefaultTableModel tableModel;

    private ServiceDAO serviceDAO;

    private final String[] CATEGORIES = {"Đồ Ăn", "Đồ Uống", "Giặt Ủi", "Spa/Massage", "Giải Trí", "Khác"};
    private final String[] UNITS = {"Cái", "Lon", "Ly", "Kg", "Suất", "Vé", "Lần", "Chai", "Tô", "Dĩa"};

    public ServiceManagerForm() {
        serviceDAO = new ServiceDAO();
        initUI();
        loadData("");
    }

    private void initUI() {
        setTitle("Quản Lý Danh Mục Dịch Vụ");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("DANH MỤC DỊCH VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tìm kiếm");

        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));

        searchPanel.add(new JLabel("<html><font color='white'>Tên dịch vụ:</font></html>"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 0));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFormPanel(), BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadData(String keyword) {
        tableModel.setRowCount(0);
        List<Service> list = serviceDAO.getListServices(keyword);
        DecimalFormat df = new DecimalFormat("#,###");

        for (Service s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getCategory(),
                    s.getUnit(),
                    df.format(s.getPrice())
            });
        }
    }

    private void addService() {
        if(txtServiceName.getText().isEmpty() || txtPrice.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên và giá dịch vụ!");
            return;
        }
        try {
            Service s = new Service();
            s.setName(txtServiceName.getText());
            s.setCategory(cbCategory.getSelectedItem().toString());
            s.setUnit(cbUnit.getSelectedItem().toString());
            s.setPrice(parsePrice(txtPrice.getText()));

            if (serviceDAO.addService(s)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tiền phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateService() {
        if (txtServiceID.getText().equals("Tự động") || txtServiceID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần sửa!");
            return;
        }
        try {
            Service s = new Service();
            s.setId(Integer.parseInt(txtServiceID.getText()));
            s.setName(txtServiceName.getText());
            s.setCategory(cbCategory.getSelectedItem().toString());
            s.setUnit(cbUnit.getSelectedItem().toString());
            s.setPrice(parsePrice(txtPrice.getText()));

            if (serviceDAO.updateService(s)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!");
        }
    }

    private void deleteService() {
        if (txtServiceID.getText().equals("Tự động") || txtServiceID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtServiceID.getText());
            if (serviceDAO.deleteService(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData("");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xóa! Dịch vụ này đã được sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private double parsePrice(String priceText) {
        return Double.parseDouble(priceText.replace(",", "").trim());
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách dịch vụ hiện có"));

        String[] cols = {"ID", "Tên Dịch Vụ", "Phân Loại", "Đơn Vị", "Đơn Giá (VND)"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) fillForm(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fillForm(int row) {
        txtServiceID.setText(tableModel.getValueAt(row, 0).toString());
        txtServiceName.setText(tableModel.getValueAt(row, 1).toString());
        cbCategory.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        cbUnit.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtPrice.setText(tableModel.getValueAt(row, 4).toString().replace(",", ""));
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin dịch vụ"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addInputRow(inputPanel, "Mã dịch vụ:", txtServiceID = new JTextField(), row++, gbc);
        txtServiceID.setEditable(false);
        txtServiceID.setText("Tự động");
        txtServiceID.setBackground(new Color(240, 240, 240));

        addInputRow(inputPanel, "Tên dịch vụ:", txtServiceName = new JTextField(), row++, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Phân loại:"), gbc);
        gbc.gridx = 1;
        cbCategory = new JComboBox<>(CATEGORIES);
        inputPanel.add(cbCategory, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        inputPanel.add(new JLabel("Đơn vị tính:"), gbc);
        gbc.gridx = 1;
        cbUnit = new JComboBox<>(UNITS);
        inputPanel.add(cbUnit, gbc);
        row++;

        addInputRow(inputPanel, "Đơn giá:", txtPrice = new JTextField(), row++, gbc);

        gbc.gridy = row; gbc.weighty = 1.0;
        inputPanel.add(new JLabel(), gbc);
        panel.add(inputPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnAdd = createButton("Thêm Mới", new Color(46, 204, 113));
        JButton btnEdit = createButton("Cập nhật", new Color(241, 196, 15));
        JButton btnDel = createButton("Xóa bỏ", new Color(231, 76, 60));
        JButton btnClear = createButton("Làm mới", Color.GRAY);

        btnAdd.addActionListener(e -> addService());
        btnEdit.addActionListener(e -> updateService());
        btnDel.addActionListener(e -> deleteService());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
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
        return btn;
    }

    private void clearForm() {
        txtServiceID.setText("Tự động");
        txtServiceName.setText("");
        cbCategory.setSelectedIndex(0);
        cbUnit.setSelectedIndex(0);
        txtPrice.setText("");
        table.clearSelection();
    }
}