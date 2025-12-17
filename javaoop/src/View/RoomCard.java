package View;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public class RoomCard extends JPanel {

    // Biến toàn cục để dùng trong hàm update
    private String currentRoomId;

    public RoomCard(String roomId, String status, String typeName, double price) {
        this.currentRoomId = roomId;

        setPreferredSize(new Dimension(180, 160));
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new LineBorder(new Color(230, 230, 230), 1));

        // --- Xử lý Màu sắc & Trạng thái ---
        Color statusColor;
        String statusVietnamese;
        Color textColor = Color.WHITE;

        String s = status.toLowerCase();

        if (s.contains("occupied") || s.contains("đang ở") || s.contains("checked in")) {
            statusColor = new Color(255, 121, 121);
            statusVietnamese = "Đang có khách";
        } else if (s.contains("booked") || s.contains("đặt trước") || s.contains("reserved")) {
            statusColor = new Color(255, 190, 118);
            statusVietnamese = "Đã đặt trước";
        } else if (s.contains("cleaning") || s.contains("dọn")) {
            statusColor = new Color(126, 214, 223);
            statusVietnamese = "Đang dọn dẹp";
        } else if (s.contains("maintenance") || s.contains("bảo trì")) {
            statusColor = new Color(99, 110, 114); // Màu xám cho bảo trì
            statusVietnamese = "Đang bảo trì";
        } else {
            statusColor = new Color(26, 188, 156);
            statusVietnamese = "Phòng trống";
        }

        // --- Header (Số phòng) ---
        JPanel header = new JPanel();
        header.setBackground(statusColor);
        header.setPreferredSize(new Dimension(0, 40));

        JLabel lblRoom = new JLabel(roomId);
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRoom.setForeground(textColor);
        header.add(lblRoom);
        add(header, BorderLayout.NORTH);

        // --- Body (Icon, Loại, Trạng thái) ---
        JPanel body = new JPanel(new GridLayout(3, 1));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel lblIcon = new JLabel("🏠", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        lblIcon.setForeground(statusColor);

        JLabel lblType = new JLabel(typeName, SwingConstants.CENTER);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblType.setForeground(new Color(80, 80, 80));

        JLabel lblStatus = new JLabel(statusVietnamese, SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(150, 150, 150));

        body.add(lblIcon);
        body.add(lblType);
        body.add(lblStatus);
        add(body, BorderLayout.CENTER);

        // --- Footer (Giá tiền) ---
        JPanel footer = new JPanel();
        footer.setBackground(new Color(250, 250, 250));
        footer.setPreferredSize(new Dimension(0, 30));

        NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceStr = currencyFormatter.format(price) + " VNĐ";

        JLabel lblPrice = new JLabel(priceStr);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPrice.setForeground(new Color(255, 159, 67));
        footer.add(lblPrice);
        add(footer, BorderLayout.SOUTH);

        // =========================================================================
        // PHẦN THÊM MỚI: MENU CHUỘT PHẢI (RIGHT CLICK)
        // =========================================================================

        // 1. Chỉ tạo menu nếu phòng KHÔNG CÓ KHÁCH (Để an toàn dữ liệu)
        if (!s.contains("occupied") && !s.contains("đang ở") && !s.contains("checked in") && !s.contains("đã đặt")) {

            JPopupMenu popup = new JPopupMenu();
            JMenuItem itemEmpty = new JMenuItem("Set: Phòng Trống (Đã dọn xong)");
            JMenuItem itemClean = new JMenuItem("Set: Đang Dọn Dẹp");
            JMenuItem itemFix = new JMenuItem("Set: Đang Bảo Trì / Sửa Chữa");

            // Gắn hành động
            itemEmpty.addActionListener(e -> updateStatus(roomId, "Available"));
            itemClean.addActionListener(e -> updateStatus(roomId, "Cleaning"));
            itemFix.addActionListener(e -> updateStatus(roomId, "Maintenance"));

            popup.add(itemEmpty);
            popup.add(itemClean);
            popup.add(itemFix);

            // 2. Tạo MouseListener để bắt sự kiện chuột phải
            MouseAdapter mouseEvent = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { showPopup(e); }
                @Override
                public void mouseReleased(MouseEvent e) { showPopup(e); }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            };

            // 3. Gắn sự kiện này vào TẤT CẢ thành phần (để click vào đâu cũng ăn)
            this.addMouseListener(mouseEvent);
            header.addMouseListener(mouseEvent);
            body.addMouseListener(mouseEvent);
            footer.addMouseListener(mouseEvent);
            lblRoom.addMouseListener(mouseEvent);
            lblIcon.addMouseListener(mouseEvent);
            lblType.addMouseListener(mouseEvent);
            lblStatus.addMouseListener(mouseEvent);
            lblPrice.addMouseListener(mouseEvent);
        }
    }

    // --- Hàm cập nhật trạng thái xuống Database ---
    private void updateStatus(String roomId, String newStatus) {
        // Gọi DAO để update
        // Lưu ý: Đảm bảo class RoomDAO của bạn đã có hàm updateRoomStatus
        if (new DAO.RoomDAO().updateRoomStatus(roomId, newStatus)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành công!\nHãy làm mới (Refresh) lại sơ đồ để thấy màu thay đổi.");

            // Nếu bạn muốn reload ngay lập tức mà không cần nút refresh,
            // bạn sẽ cần gọi ngược lại hàm loadRooms() của DashboardView.
            // Nhưng cách đơn giản nhất là thông báo người dùng bấm vào Tab khác rồi quay lại.
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}