package View;

import DAO.RoomTypeDAO;
import models.RoomType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

public class RoomTypeManager extends JFrame {

    private JTextField txtTypeID, txtTypeName, txtPrice;
    private JTextArea txtDesc;
    private JTable table;
    private DefaultTableModel tableModel;

    private RoomTypeDAO dao;

    // Màu sắc giao diện
    private final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private final Color BTN_ADD_COLOR = new Color(46, 204, 113);
    private final Color BTN_EDIT_COLOR = new Color(52, 152, 219);
    private final Color BTN_DEL_COLOR = new Color(231, 76, 60);

    public RoomTypeManager() {
        dao = new RoomTypeDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Loại Phòng");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(100, 60));
        JLabel lblTitle = new JLabel("DANH MỤC LOẠI PHÒNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(10, 0, 0, 0));
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        mainPanel.add(createFormPanel());
        mainPanel.add(createListPanel());
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Thông tin loại phòng",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Mã loại:"), gbc);
        gbc.gridx = 1;
        txtTypeID = new JTextField(15);
        txtTypeID.setEditable(false);
        txtTypeID.setBackground(new Color(230, 230, 230));
        inputPanel.add(txtTypeID, gbc);

        // Tên loại
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Tên loại phòng:"), gbc);
        gbc.gridx = 1;
        txtTypeName = new JTextField(15);
        inputPanel.add(txtTypeName, gbc);

        // Giá
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Giá theo đêm (VND):"), gbc);
        gbc.gridx = 1;
        txtPrice = new JTextField(15);
        inputPanel.add(txtPrice, gbc);

        // Mô tả
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(new JLabel("Mô tả tiện ích:"), gbc);
        gbc.gridx = 1;
        txtDesc = new JTextArea(5, 15);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        inputPanel.add(scrollDesc, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = createBtn("Thêm", BTN_ADD_COLOR);
        JButton btnEdit = createBtn("Sửa", BTN_EDIT_COLOR);
        JButton btnDel = createBtn("Xóa", BTN_DEL_COLOR);
        JButton btnClear = createBtn("Làm mới", Color.GRAY);

        btnAdd.addActionListener(e -> addRoomType());
        btnEdit.addActionListener(e -> updateRoomType());
        btnDel.addActionListener(e -> deleteRoomType());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR), "Danh sách hiện có",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));

        String[] cols = {"ID", "Tên loại", "Giá (VND)", "Mô tả"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Sự kiện click vào bảng -> đổ dữ liệu lên form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtTypeID.setText(tableModel.getValueAt(row, 0).toString());
                    txtTypeName.setText(tableModel.getValueAt(row, 1).toString());

                    // Xử lý chuỗi giá tiền (bỏ dấu phẩy để hiển thị lên ô nhập)
                    String priceStr = tableModel.getValueAt(row, 2).toString().replace(",", "");
                    txtPrice.setText(priceStr);

                    Object desc = tableModel.getValueAt(row, 3);
                    txtDesc.setText(desc != null ? desc.toString() : "");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void loadData() {
        tableModel.setRowCount(0);
        List<RoomType> list = dao.getAllRoomTypes();
        DecimalFormat df = new DecimalFormat("#,###");

        for (RoomType rt : list) {
            tableModel.addRow(new Object[]{
                    rt.getTypeId(),     // Model trả về String (hợp lệ)
                    rt.getTypeName(),
                    df.format(rt.getPrice()),
                    rt.getDescription()
            });
        }
    }

    private void addRoomType() {
        if(txtTypeName.getText().isEmpty() || txtPrice.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên và Giá!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String name = txtTypeName.getText();
            double price = Double.parseDouble(txtPrice.getText().replace(",", ""));
            String desc = txtDesc.getText();

            // Gọi DAO
            if (dao.addRoomType(name, price, desc)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá tiền phải là số!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoomType() {
        if (txtTypeID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phòng cần sửa!");
            return;
        }
        try {
            // DAO yêu cầu ID là int, nhưng View đang hiển thị String -> Phải ép kiểu
            int id = Integer.parseInt(txtTypeID.getText());
            String name = txtTypeName.getText();
            double price = Double.parseDouble(txtPrice.getText().replace(",", ""));
            String desc = txtDesc.getText();

            if (dao.updateRoomType(id, name, price, desc)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ (ID hoặc Giá tiền)!");
        }
    }

    private void deleteRoomType() {
        if (txtTypeID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại phòng cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa? (Nếu loại phòng này đang được dùng, không thể xóa)",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Ép kiểu ID về int để gọi DAO
                int id = Integer.parseInt(txtTypeID.getText());

                if (dao.deleteRoomType(id)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa! Có thể loại phòng đang được sử dụng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        txtTypeID.setText("");
        txtTypeName.setText("");
        txtPrice.setText("");
        txtDesc.setText("");
        table.clearSelection();
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(90, 35));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomTypeManager().setVisible(true));
    }
}