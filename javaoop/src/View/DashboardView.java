package View;

import DAO.RoomDAO;
import models.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DashboardView extends JFrame {

    private RoomDAO roomDAO;
    
    // [QUAN TRỌNG] Các thành phần điều hướng
    private CardLayout cardLayout;       // Quản lý chuyển cảnh
    private JPanel mainContentPanel;     // Panel chứa tất cả các màn hình con
    private JPanel roomGrid;             // Grid chứa các thẻ phòng (để load dữ liệu)

    // Màu sắc
    private final Color SIDEBAR_BG = new Color(44, 62, 80);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color HOVER_COLOR = new Color(52, 73, 94);

    public DashboardView() {
        roomDAO = new RoomDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Hệ thống Quản lý Khách sạn");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR (BÊN TRÁI) ---
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // --- 2. MAIN CONTENT (BÊN PHẢI) - DÙNG CARDLAYOUT ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);

        // >> Tạo các màn hình con <<
        
        // Màn hình 1: Trang chủ (Mặc định hiển thị đầu tiên)
        JPanel homePanel = createHomePanel();
        mainContentPanel.add(homePanel, "HOME");

        // Màn hình 2: Sơ đồ phòng (Chuyển code cũ vào đây)
        JPanel roomMapPanel = createRoomMapPanel();
        mainContentPanel.add(roomMapPanel, "ROOM_MAP");

        // ... Bạn có thể thêm các màn hình khác (Khách hàng, Nhân viên) tại đây ...

        add(mainContentPanel, BorderLayout.CENTER);

        // Mặc định hiển thị màn hình HOME
        cardLayout.show(mainContentPanel, "HOME");
    }

    // ========================================================================
    //                          PHẦN GIAO DIỆN (UI)
    // ========================================================================

    // --- A. TẠO SIDEBAR ---
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(SIDEBAR_BG);
        logoPanel.setMaximumSize(new Dimension(260, 80));
        JLabel lblTitle = new JLabel("HOTEL MANAGER");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoPanel.add(lblTitle);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        // MENU ITEMS
        // Lưu ý: Logic chuyển trang nằm trong các hàm này
        addSingleMenu(sidebar, "  Màn Hình Chính", "HOME"); // Truyền key "HOME"
        
        addDropdownMenu(sidebar, "  Quản Lý Phòng", new String[]{"Sơ Đồ Phòng", "Loại Phòng"});
        addDropdownMenu(sidebar, "  Khách Hàng", new String[]{"Thông Tin KH", "Dịch Vụ"});
        addDropdownMenu(sidebar, "  Nhân Viên", new String[]{"Danh Sách", "Ca Làm"});
        addSingleMenu(sidebar, "  Dịch Vụ", "SERVICE");
        addSingleMenu(sidebar, "  Báo Cáo - Thống Kê", "REPORT");

        sidebar.add(Box.createVerticalGlue());
        addSingleMenu(sidebar, "  Đăng Xuất", "LOGOUT");
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    // --- B. TẠO MÀN HÌNH CHÍNH (HOME) ---
   // --- B. TẠO MÀN HÌNH CHÍNH (HOME) - PHIÊN BẢN ĐẸP ---
    private JPanel createHomePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245)); // Nền xám rất nhạt hiện đại

        // 1. HEADER (Gradient Banner)
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient từ Xanh đậm sang Xanh nhạt
                GradientPaint gp = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), 0, new Color(52, 152, 219));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        banner.setPreferredSize(new Dimension(100, 120));
        banner.setLayout(null);

        JLabel lblWelcome = new JLabel("Xin chào, Quản Lý!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(30, 25, 400, 40);
        banner.add(lblWelcome);

        JLabel lblDate = new JLabel("Hôm nay là: " + java.time.LocalDate.now());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDate.setForeground(new Color(230, 230, 230));
        lblDate.setBounds(30, 65, 300, 20);
        banner.add(lblDate);

        mainPanel.add(banner, BorderLayout.NORTH);

        // 2. BODY (Stats + Table)
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(new Color(240, 242, 245));
        bodyPanel.setBorder(new EmptyBorder(20, 30, 20, 30)); // Căn lề

        // --- Hàng Thống Kê ---
        JPanel statsContainer = new JPanel(new GridLayout(1, 3, 20, 0)); // 3 cột, cách nhau 20px
        statsContainer.setBackground(new Color(240, 242, 245));
        statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        // Tạo các thẻ đẹp hơn
        statsContainer.add(createModernStatCard("TỔNG SỐ PHÒNG", "20", "🛏", new Color(52, 152, 219)));
        statsContainer.add(createModernStatCard("ĐANG SỬ DỤNG", "12", "👤", new Color(231, 76, 60)));
        statsContainer.add(createModernStatCard("PHÒNG TRỐNG", "08", "✅", new Color(46, 204, 113)));

        bodyPanel.add(statsContainer);
        bodyPanel.add(Box.createVerticalStrut(30)); // Khoảng cách

        // --- Hàng "Hoạt động gần đây" & "Truy cập nhanh" ---
        JPanel bottomContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomContainer.setBackground(new Color(240, 242, 245));

        // Bảng bên trái: Hoạt động gần đây
        bottomContainer.add(createRecentActivityPanel());
        
        // Panel bên phải: Chức năng nhanh
        bottomContainer.add(createQuickActionsPanel());

        bodyPanel.add(bottomContainer);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    // Helper tạo thẻ thống kê hiện đại (Modern Card)
    private JPanel createModernStatCard(String title, String number, String icon, Color iconBgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Bo viền và đổ bóng nhẹ (dùng MatteBorder giả lập)
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 4, 0, iconBgColor), // Viền màu dưới đáy
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Icon bên trái
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(iconBgColor);
        lblIcon.setPreferredSize(new Dimension(60, 60));
        
        // Nội dung bên phải
        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBackground(Color.WHITE);
        
        JLabel lblNum = new JLabel(number);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblNum.setForeground(new Color(50, 50, 50));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);
        
        rightPanel.add(lblNum);
        rightPanel.add(lblTitle);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(rightPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentActivityPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Hoạt Động Gần Đây");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(SIDEBAR_BG);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        p.add(title, BorderLayout.NORTH);

        // Dữ liệu giả lập bảng
        String[] columns = {"Khách Hàng", "Phòng", "Thời Gian", "Trạng Thái"};
        Object[][] data = {
            {"Nguyễn Văn A", "101", "08:30 AM", "Check-in"},
            {"Trần Thị B", "205", "09:15 AM", "Check-out"},
            {"Lê Văn C", "302", "10:00 AM", "Booking"},
            {"Phạm Văn D", "104", "10:45 AM", "Dọn dẹp"}
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel createQuickActionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Chức Năng Nhanh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(SIDEBAR_BG);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        p.add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 hàng 2 cột
        btnPanel.setBackground(Color.WHITE);

        btnPanel.add(createActionBtn("Check In ", new Color(52, 152, 219)));
        btnPanel.add(createActionBtn("Check Out ", new Color(231, 76, 60)));
        btnPanel.add(createActionBtn("Xuất Hóa Đơn", new Color(241, 196, 15)));
        btnPanel.add(createActionBtn("Báo Cáo Ngày", new Color(155, 89, 182)));

        p.add(btnPanel, BorderLayout.CENTER);
        return p;
    }

    private JButton createActionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Helper tạo thẻ thống kê cho màn hình chính
    private JPanel createStatCard(String title, String number, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 150));
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblNumber = new JLabel(number, SwingConstants.CENTER);
        lblNumber.setFont(new Font("Arial", Font.BOLD, 48));
        lblNumber.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        card.add(lblNumber, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        return card;
    }

    // --- C. TẠO MÀN HÌNH SƠ ĐỒ PHÒNG (Code cũ của bạn chuyển vào đây) ---
    private JPanel createRoomMapPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // 1. Header & Legend
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel lblMap = new JLabel("SƠ ĐỒ PHÒNG", SwingConstants.LEFT);
        lblMap.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMap.setForeground(SIDEBAR_BG);
        topPanel.add(lblMap, BorderLayout.NORTH);

        // Thêm Legend (Chú thích)
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legend.setBackground(Color.WHITE);
        legend.add(createLegendItem("Trống", new Color(26, 188, 156)));
        legend.add(createLegendItem("Đang ở", new Color(255, 121, 121)));
        legend.add(createLegendItem("Đặt trước", new Color(255, 190, 118)));
        legend.add(createLegendItem("Dọn dẹp", new Color(126, 214, 223)));
        topPanel.add(legend, BorderLayout.SOUTH);

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // 2. Grid Phòng
        roomGrid = new JPanel(new GridLayout(0, 5, 15, 15));
        roomGrid.setBackground(Color.WHITE);
        roomGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Load dữ liệu
        loadRoomCards();

        // Wrap vào ScrollPane
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(roomGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        return contentPanel;
    }

    private JLabel createLegendItem(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.fillRect(x, y, 16, 16);
            }
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
        });
        lbl.setIconTextGap(8);
        return lbl;
    }

    // ========================================================================
    //                          XỬ LÝ LOGIC & MENU
    // ========================================================================

    // Load dữ liệu thẻ phòng (Giữ nguyên logic của bạn)
    private void loadRoomCards() {
        if (roomGrid == null) return;
        roomGrid.removeAll();

        List<Room> listRooms = roomDAO.getAllRooms();
        for (Room r : listRooms) {
            String roomID = r.getRoomID();
            String status = r.getStatus();
            String typeName = r.getRoomType().getTypeName();
            double price = r.getRoomType().getPrice();
            roomGrid.add(new RoomCard(roomID, status, typeName, price));
        }
        roomGrid.revalidate();
        roomGrid.repaint();
    }

    // Thêm Menu Đơn (Xử lý chuyển trang HOME, LOGOUT)
    private void addSingleMenu(JPanel sidebar, String text, String targetPanelKey) {
        JButton btn = createBaseButton(text);
        btn.addActionListener(e -> {
            if ("LOGOUT".equals(targetPanelKey)) {
                handleLogout();
            } else if ("HOME".equals(targetPanelKey)) {
                // Chuyển về màn hình chính
                cardLayout.show(mainContentPanel, "HOME");
            } else {
                System.out.println("Chức năng chưa phát triển: " + text);
            }
        });
        sidebar.add(btn);
    }

    // Thêm Menu Xổ Xuống (Xử lý chuyển trang ROOM_MAP)
    // --- SỬA LẠI HÀM NÀY TRONG DashboardView.java ---
    private void addDropdownMenu(JPanel sidebar, String text, String[] subs) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(SIDEBAR_BG);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnParent = createBaseButton(text + " ▼");
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(SIDEBAR_BG);
        subPanel.setVisible(false);
        subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String s : subs) {
            JButton subBtn = createBaseButton("      • " + s);
            subBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subBtn.setForeground(new Color(200, 200, 200));

            // --- ĐOẠN SỬA LỖI Ở ĐÂY ---
            subBtn.addActionListener(e -> {
                // In ra console để kiểm tra xem bạn đang bấm nút nào (Debug)
                System.out.println("Đang nhấn vào: " + s);

                // Dùng equalsIgnoreCase để không phân biệt hoa thường
                if (s.trim().equalsIgnoreCase("Sơ Đồ Phòng")) {
                    // Load lại dữ liệu mới nhất
                    loadRoomCards(); 
                    // Chuyển card
                    cardLayout.show(mainContentPanel, "ROOM_MAP");
                    System.out.println("-> Đã chuyển sang màn hình ROOM_MAP");
                } else {
                    System.out.println("-> Chức năng chưa có code xử lý");
                }
            });
            // ---------------------------

            subPanel.add(subBtn);
        }

        btnParent.addActionListener(e -> {
            subPanel.setVisible(!subPanel.isVisible());
            btnParent.setText(subPanel.isVisible() ? text + " ▲" : text + " ▼");
            sidebar.revalidate();
            sidebar.repaint();
        });

        group.add(btnParent);
        group.add(subPanel);
        sidebar.add(group);
    }
    private JButton createBaseButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_COLOR); }
            public void mouseExited(MouseEvent e) { btn.setBackground(SIDEBAR_BG); }
        });
        return btn;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
            this, "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginView().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardView().setVisible(true));
    }
}