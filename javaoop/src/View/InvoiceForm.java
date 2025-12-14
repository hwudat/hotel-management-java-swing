package View;

import DAO.InvoiceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class InvoiceForm extends JFrame {

    private JTable tblItems;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblTax, lblFinalTotal;
    private JLabel lblInvoiceDate, lblInvoiceID;

    private JLabel lblCustomerName, lblRoomNumber, lblRoomType, lblRoomPrice;
    private JTextField txtSearchBill;
    
    private InvoiceDAO invoiceDAO;

    public InvoiceForm() {
        invoiceDAO = new InvoiceDAO(); 
        initUI();
    }

    private void initUI() {
        setTitle("Chi Tiết Hóa Đơn - Hotel Manager");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(240, 240, 240));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        topBar.add(new JLabel("Nhập mã phòng cần xuất hóa đơn: "));
        txtSearchBill = new JTextField(15);
        JButton btnSearch = new JButton("Tìm kiếm");
       
        btnSearch.addActionListener(e -> loadDataFromDB(txtSearchBill.getText().trim()));
        
        topBar.add(txtSearchBill);
        topBar.add(btnSearch);

        add(topBar, BorderLayout.NORTH);

        JPanel paperPanel = new JPanel(new BorderLayout());
        paperPanel.setBackground(Color.WHITE);
        paperPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));

        JPanel hotelInfo = new JPanel(new GridLayout(3, 1));
        hotelInfo.setBackground(Color.WHITE);
        JLabel lblHotelName = new JLabel("LUXURY HOTEL");
        lblHotelName.setFont(new Font("Serif", Font.BOLD, 24));
        hotelInfo.add(lblHotelName);
        hotelInfo.add(new JLabel("Địa chỉ: 123 Đường ABC, Quận H, Hà Nội"));
        hotelInfo.add(new JLabel("Hotline: 0909 123 456"));

        JPanel billInfo = new JPanel(new GridLayout(3, 1));
        billInfo.setBackground(Color.WHITE);
        lblInvoiceID = new JLabel("HÓA ĐƠN #: ---", SwingConstants.RIGHT);
        lblInvoiceID.setFont(new Font("Arial", Font.BOLD, 14));
        lblInvoiceDate = new JLabel("Ngày in: " + LocalDate.now(), SwingConstants.RIGHT);

        billInfo.add(lblInvoiceID);
        billInfo.add(lblInvoiceDate);

        headerPanel.add(hotelInfo, BorderLayout.WEST);
        headerPanel.add(billInfo, BorderLayout.EAST);

        JPanel infoGrid = new JPanel(new GridLayout(2, 2, 20, 10));
        infoGrid.setBackground(Color.WHITE);
        infoGrid.setBorder(new EmptyBorder(20, 0, 20, 0));

        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font boldFont = new Font("Arial", Font.BOLD, 14);

        lblCustomerName = new JLabel("Khách hàng: ---");
        lblCustomerName.setFont(boldFont);

        lblRoomNumber = new JLabel("Phòng số: ---");
        lblRoomNumber.setFont(labelFont);

        lblRoomType = new JLabel("Loại phòng: ---");
        lblRoomType.setFont(labelFont);

        lblRoomPrice = new JLabel("Đơn giá: --- /đêm");
        lblRoomPrice.setFont(labelFont);

        infoGrid.add(lblCustomerName);
        infoGrid.add(lblRoomNumber);
        infoGrid.add(lblRoomType);
        infoGrid.add(lblRoomPrice);

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setBackground(Color.WHITE);
        topContent.add(headerPanel, BorderLayout.NORTH);
        topContent.add(infoGrid, BorderLayout.CENTER);

        paperPanel.add(topContent, BorderLayout.NORTH);

        // >> BODY (Bảng chi tiết)
        String[] cols = {"STT", "Dịch vụ", "Đơn giá", "Số lượng", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0);
        tblItems = new JTable(tableModel);
        tblItems.setFillsViewportHeight(true);
        tblItems.setRowHeight(25);
        tblItems.setShowVerticalLines(false);
        tblItems.setGridColor(new Color(230, 230, 230));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblItems.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblItems.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(tblItems);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        paperPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        lblTotal = createTotalLabel("Cộng tiền hàng:", "0");
        lblTax = createTotalLabel("Thuế VAT (0%):", "0");
        lblFinalTotal = createTotalLabel("TỔNG THANH TOÁN:", "0");
        lblFinalTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblFinalTotal.setForeground(Color.RED);

        footerPanel.add(lblTotal);
        footerPanel.add(lblTax);
        footerPanel.add(lblFinalTotal);

        paperPanel.add(footerPanel, BorderLayout.SOUTH);
        add(paperPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setBackground(new Color(240, 240, 240));

        JButton btnPrint = new JButton("🖨 In Hóa Đơn");
        JButton btnClose = new JButton("Đóng");

        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(120, 35));
        
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang gửi lệnh in..."));

        btnClose.addActionListener(e -> dispose());

        actionPanel.add(btnPrint);
        actionPanel.add(btnClose);

        add(actionPanel, BorderLayout.SOUTH);
    }

    private JLabel createTotalLabel(String text, String value) {
        JLabel lbl = new JLabel(text + "       " + value, SwingConstants.RIGHT);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        return lbl;
    }

    public void loadDataFromDB(String roomId) {
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!");
            return;
        }

        Object[] info = invoiceDAO.getInvoiceHeaderInfo(roomId);
        
        if (info != null) {
            String cusName = (String) info[0];
            String roomNum = (String) info[1];
            String typeName = (String) info[2];
            double price = (double) info[3];
            long days = (long) info[4];
            double totalRoom = price * days;

            NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            lblInvoiceID.setText("HÓA ĐƠN #: INV-" + System.currentTimeMillis() % 10000); // Mã giả lập
            lblCustomerName.setText("Khách hàng: " + cusName);
            lblRoomNumber.setText("Phòng số: " + roomNum);
            lblRoomType.setText("Loại phòng: " + typeName);
            lblRoomPrice.setText("Đơn giá: " + currency.format(price) + "/đêm");

            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{"1", "Tiền phòng (" + days + " ngày)", currency.format(price), days, currency.format(totalRoom)});

            lblTotal.setText("Cộng tiền hàng:       " + currency.format(totalRoom));
            lblTax.setText("Thuế VAT (0%):        0 đ");
            lblFinalTotal.setText("TỔNG THANH TOÁN:      " + currency.format(totalRoom));
            
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu thuê phòng hoặc phòng đang trống: " + roomId);
        }
    }
}