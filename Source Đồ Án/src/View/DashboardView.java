package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardView extends JFrame {

    public DashboardView() {
        setTitle("Hệ thống Quản lý Khách sạn - Dashboard");
        setSize(1200, 750); // Kích thước lớn giống ảnh
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- A. SIDEBAR (BÊN TRÁI) ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(44, 62, 80)); // Màu xanh đen đậm
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo hoặc Tiêu đề Sidebar
        JLabel lblTitle = new JLabel(" HOTEL MANAGER", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(new EmptyBorder(20, 0, 30, 0));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitle);

        // Các nút Menu
        addMenuButton(sidebar, "🏠  Màn Hình Chính");
        addMenuButton(sidebar, "🛏  Quản Lý Phòng");
        addMenuButton(sidebar, "👥  Khách Hàng");
        addMenuButton(sidebar, "👔  Nhân Viên");
        addMenuButton(sidebar, "🛠  Dịch Vụ");
        addMenuButton(sidebar, "📊  Báo Cáo");

        // Đẩy các nút lên trên, khoảng trống ở dưới
        sidebar.add(Box.createVerticalGlue());
        addMenuButton(sidebar, "🚪  Đăng Xuất");

        add(sidebar, BorderLayout.WEST);


        // --- B. CONTENT (BÊN PHẢI) ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // 1. Header của Content (Thanh tiêu đề + Filter)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel lblMapTitle = new JLabel("SƠ ĐỒ PHÒNG");
        lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMapTitle.setForeground(new Color(44, 62, 80));

        // Panel chứa các nút chức năng (Thêm phòng, Lọc...)
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolBar.setBackground(Color.WHITE);
        JButton btnAddRoom = new JButton("+ Thêm Phòng");
        btnAddRoom.setBackground(new Color(52, 152, 219));
        btnAddRoom.setForeground(Color.WHITE);
        toolBar.add(btnAddRoom);

        headerPanel.add(lblMapTitle, BorderLayout.WEST);
        headerPanel.add(toolBar, BorderLayout.EAST);

        // Panel chú thích màu sắc (Legend)
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.setBackground(new Color(236, 240, 241));
        legendPanel.add(createLegendLabel("Phòng Trống", new Color(52, 73, 94)));
        legendPanel.add(createLegendLabel("Đang Ở", new Color(155, 89, 182)));
        legendPanel.add(createLegendLabel("Đặt Trước", new Color(46, 204, 113)));
        legendPanel.add(createLegendLabel("Sửa Chữa", new Color(231, 76, 60)));

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(legendPanel, BorderLayout.SOUTH);
        contentPanel.add(topContainer, BorderLayout.NORTH);


        // 2. Lưới chứa danh sách phòng (Room Grid)
        JPanel roomGrid = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 cột, khoảng cách 15px
        roomGrid.setBackground(Color.WHITE);
        roomGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- TẠO DỮ LIỆU GIẢ ĐỂ TEST GIAO DIỆN ---
        // (Sau này bạn sẽ thay đoạn này bằng vòng lặp lấy từ List<Room> của DAO)
        roomGrid.add(new RoomCard("P101", "Occupied", "Nguyễn Văn An"));
        roomGrid.add(new RoomCard("P102", "Available", ""));
        roomGrid.add(new RoomCard("P103", "Booked", "Trần Thị B"));
        roomGrid.add(new RoomCard("P104", "Available", ""));
        roomGrid.add(new RoomCard("P105", "Maintenance", ""));
        roomGrid.add(new RoomCard("P201", "Occupied", "Lê Văn C"));
        roomGrid.add(new RoomCard("P202", "Available", ""));
        roomGrid.add(new RoomCard("P203", "Available", ""));
        roomGrid.add(new RoomCard("P204", "Occupied", "Phạm Văn D"));

        // Cho vào thanh cuộn (Scroll Pane) nếu phòng quá nhiều
        JScrollPane scrollPane = new JScrollPane(roomGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Hàm phụ trợ tạo nút menu sidebar
    private void addMenuButton(JPanel panel, String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 45));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Căn lề trái
        btn.setBorder(new EmptyBorder(0, 30, 0, 0)); // Padding chữ

        // Hiệu ứng hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(44, 62, 80));
            }
        });
        panel.add(btn);
    }

    // Hàm phụ trợ tạo chú thích màu
    private JLabel createLegendLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding cho label
        return lbl;
    }
    public static void main(String[] args) {
        // Chạy giao diện trên luồng sự kiện Swing
        javax.swing.SwingUtilities.invokeLater(() -> {
            new DashboardView().setVisible(true);
        });
    }
}