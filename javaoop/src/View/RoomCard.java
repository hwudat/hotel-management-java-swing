package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomCard extends JPanel {
    private String roomId;
    private String status; // Available, Occupied, Booked, Maintenance

    public RoomCard(String roomId, String status, String customerName) {
        this.roomId = roomId;
        this.status = status;

        // Cấu hình kích thước và Layout cho thẻ phòng
        setPreferredSize(new Dimension(180, 120));
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Margin bên trong

        // 1. Màu nền dựa trên trạng thái
        Color bgColor;
        String statusText;
        String iconText = ""; // Giả lập icon bằng ký tự

        switch (status) {
            case "Occupied": // Đang ở (Màu tím/hồng như ảnh)
                bgColor = new Color(155, 89, 182);
                statusText = "Đang ở";
                iconText = "👤";
                break;
            case "Booked": // Đặt trước (Màu xanh lá)
                bgColor = new Color(46, 204, 113);
                statusText = "Đặt trước";
                iconText = "📅";
                break;
            case "Maintenance": // Sửa chữa (Màu đỏ)
                bgColor = new Color(231, 76, 60);
                statusText = "Sửa chữa";
                iconText = "🔧";
                break;
            default: // Trống (Màu xám tối như ảnh)
                bgColor = new Color(52, 73, 94);
                statusText = "Phòng trống";
                iconText = "🏠";
                customerName = "Trống";
        }
        setBackground(bgColor);

        // 2. Phần Header: Icon + Tên phòng
        JLabel lblHeader = new JLabel(iconText + " " + roomId, SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        add(lblHeader, BorderLayout.NORTH);

        // 3. Phần Giữa: Tên khách hàng & Trạng thái
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.setOpaque(false); // Trong suốt để thấy màu nền

        JLabel lblName = new JLabel(customerName, SwingConstants.CENTER);
        lblName.setForeground(Color.WHITE);
        lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblStatus = new JLabel(statusText, SwingConstants.CENTER);
        lblStatus.setForeground(new Color(236, 240, 241)); // Màu trắng nhạt
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        centerPanel.add(new JLabel("")); // Khoảng trống
        centerPanel.add(lblName);
        centerPanel.add(lblStatus);

        add(centerPanel, BorderLayout.CENTER);

        // 4. Hiệu ứng khi di chuột vào (Hover)
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBackground(bgColor.brighter()); // Sáng lên tí
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(bgColor); // Trả về màu cũ
            }
        });
    }
}