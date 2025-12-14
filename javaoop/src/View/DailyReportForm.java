package View;

import DAO.ReportDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DailyReportForm extends JFrame {

    private final Color HEADER_BG = new Color(44, 62, 80); 
    private JLabel lblReportTime;
    
    private ReportDAO reportDAO;

    public DailyReportForm() {
        reportDAO = new ReportDAO(); 
        initUI();
    }

    private void initUI() {
        setTitle("Báo Cáo Hoạt Động Trong Ngày");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("BÁO CÁO TỔNG HỢP KHÁCH SẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblReportTime = new JLabel("Thời gian xuất báo cáo: " + timeNow);
        lblReportTime.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblReportTime.setForeground(new Color(200, 200, 200));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblReportTime, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane.addTab("  Check-In Hôm Nay  ", null, createCheckInPanel(), "Danh sách khách đến nhận phòng");

        tabbedPane.addTab("  Check-Out Hôm Nay  ", null, createCheckOutPanel(), "Danh sách khách đã trả phòng");

        tabbedPane.addTab("  Phòng Đang Sử Dụng  ", null, createOccupiedPanel(), "Danh sách phòng đang có khách ở");

        add(tabbedPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnPrint = new JButton("🖨 Xuất Excel / In");
        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(150, 35));

        JButton btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dispose());

        footerPanel.add(btnPrint);
        footerPanel.add(btnClose);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createCheckInPanel() {
        String[] cols = {"Phòng", "Khách hàng", "CMND/CCCD", "Loại phòng", "Giờ Check-in", "Nhân Viên"};
       
        Object[][] data = reportDAO.getTodayCheckInList();
        
       
        if (data == null || data.length == 0) {
            data = new Object[][]{{"-", "Không có dữ liệu", "-", "-", "-", "-"}};
        }
        return createTablePanel(cols, data);
    }

    private JPanel createCheckOutPanel() {
        String[] cols = {"Phòng", "Khách hàng", "Giờ vào", "Giờ ra"};
      
        Object[][] data = reportDAO.getTodayCheckOutList();
        
        if (data == null || data.length == 0) {
            data = new Object[][]{{"-", "Không có dữ liệu", "-", "-"}};
        }
        return createTablePanel(cols, data);
    }

    private JPanel createOccupiedPanel() {
        String[] cols = {"Phòng", "Loại phòng", "Khách hàng", "Ngày Check-in"};
     
        Object[][] data = reportDAO.getOccupiedRoomsList();
        
        if (data == null || data.length == 0) {
            data = new Object[][]{{"-", "-", "Không có dữ liệu", "-"}};
        }
        return createTablePanel(cols, data);
    }

    private JPanel createTablePanel(String[] columns, Object[][] data) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);

        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(0, 35));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
}