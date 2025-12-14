package View;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class RoomCard extends JPanel {

    public RoomCard(String roomId, String status, String typeName, double price) {
       
        setPreferredSize(new Dimension(180, 160));
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
       
        setBorder(new LineBorder(new Color(230, 230, 230), 1)); 

        Color statusColor;
        String statusVietnamese;
        Color textColor = Color.WHITE; 

        String s = status.toLowerCase();

        if (s.contains("occupied") || s.contains("đang ở")) {
           
            statusColor = new Color(255, 121, 121); 
            statusVietnamese = "Đang có khách";
            
        } else if (s.contains("booked") || s.contains("đặt trước")) {
          
            statusColor = new Color(255, 190, 118); 
            statusVietnamese = "Đã đặt trước";
            
        } else if (s.contains("cleaning") || s.contains("dọn")) {
           
            statusColor = new Color(126, 214, 223); 
            statusVietnamese = "Đang dọn dẹp";
            
        } else {
            
            statusColor = new Color(26, 188, 156); 
            
            statusVietnamese = "Phòng trống";
        }

        JPanel header = new JPanel();
        header.setBackground(statusColor);
        header.setPreferredSize(new Dimension(0, 40));
        
        JLabel lblRoom = new JLabel(roomId);
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRoom.setForeground(textColor);
        header.add(lblRoom);
        add(header, BorderLayout.NORTH);

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
    }
}