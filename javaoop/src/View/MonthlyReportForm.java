package View;

import DAO.ReportDAO; 
import models.DailyStat; 

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;

public class MonthlyReportForm extends JFrame {

    private JComboBox<Integer> cbMonth;
    private JComboBox<Integer> cbYear;
    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel lblTotalRevenue, lblTotalBookings, lblServiceRevenue, lblRoomRevenue;

    private ReportDAO reportDAO; 

    private final Color PRIMARY_COLOR = new Color(44, 62, 80);

    public MonthlyReportForm() {
        reportDAO = new ReportDAO(); 
        initUI();
     
        loadStatistics(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
    }

    private void initUI() {
        setTitle("Báo Cáo Thống Kê Doanh Thu Tháng");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        headerPanel.setBackground(PRIMARY_COLOR);

        JLabel lblTitle = new JLabel("THỐNG KÊ THÁNG:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        cbMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cbMonth.addItem(i);
        cbMonth.setSelectedItem(LocalDate.now().getMonthValue());

        cbYear = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) cbYear.addItem(i);
        cbYear.setSelectedItem(currentYear);

        JButton btnView = new JButton("Xem Báo Cáo");
        btnView.setBackground(new Color(52, 152, 219));
        btnView.setForeground(Color.WHITE);
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnView.addActionListener(e -> {
            int m = (int) cbMonth.getSelectedItem();
            int y = (int) cbYear.getSelectedItem();
            loadStatistics(m, y);
        });

        headerPanel.add(lblTitle);
        headerPanel.add(new JLabel("<html><font color='white'>Tháng:</font></html>"));
        headerPanel.add(cbMonth);
        headerPanel.add(new JLabel("<html><font color='white'>Năm:</font></html>"));
        headerPanel.add(cbYear);
        headerPanel.add(btnView);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(0, 120));

        lblTotalRevenue = new JLabel("0 VND");
        lblTotalBookings = new JLabel("0");
        lblRoomRevenue = new JLabel("0 VND");
        lblServiceRevenue = new JLabel("0 VND");

        cardsPanel.add(createCard("TỔNG DOANH THU", lblTotalRevenue, new Color(46, 204, 113), "💰"));
        cardsPanel.add(createCard("SỐ LƯỢT ĐẶT", lblTotalBookings, new Color(52, 152, 219), "🔖"));
        cardsPanel.add(createCard("DOANH THU PHÒNG", lblRoomRevenue, new Color(155, 89, 182), "🛏"));
        cardsPanel.add(createCard("DOANH THU DỊCH VỤ", lblServiceRevenue, new Color(230, 126, 34), "☕"));

        mainPanel.add(cardsPanel, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Chi tiết doanh thu theo ngày",
                0, 0, new Font("Segoe UI", Font.BOLD, 14)));

        String[] cols = {"Ngày", "Số phòng check-out", "Doanh thu phòng", "Doanh thu dịch vụ", "Tổng cộng"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("Xuất Excel");
        JButton btnClose = new JButton("Đóng");

        btnClose.addActionListener(e -> dispose());
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển!"));

        footer.add(btnExport);
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(new Color(50, 50, 50));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcon.setForeground(color);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel);
        textPanel.add(lblTitle);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private void loadStatistics(int month, int year) {
        tableModel.setRowCount(0);
        DecimalFormat df = new DecimalFormat("#,###");

        long sumTotalRoom = 0;
        long sumTotalService = 0;
        int sumTotalBook = 0;

        Map<Integer, DailyStat> stats = reportDAO.getMonthlyReport(month, year);

        for (int d = 1; d <= stats.size(); d++) {
            DailyStat s = stats.get(d);
            
            tableModel.addRow(new Object[]{
                    d + "/" + month + "/" + year,
                    s.getBookingCount(),
                    df.format(s.getRoomRevenue()),
                    df.format(s.getServiceRevenue()),
                    df.format(s.getTotalRevenue())
            });

            sumTotalBook += s.getBookingCount();
            sumTotalRoom += s.getRoomRevenue();
            sumTotalService += s.getServiceRevenue();
        }

        lblTotalBookings.setText(String.valueOf(sumTotalBook));
        lblRoomRevenue.setText(df.format(sumTotalRoom) + " VND");
        lblServiceRevenue.setText(df.format(sumTotalService) + " VND");
        lblTotalRevenue.setText(df.format(sumTotalRoom + sumTotalService) + " VND");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MonthlyReportForm().setVisible(true));
    }
}