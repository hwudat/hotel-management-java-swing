package View;

import DAO.InvoiceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
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

        // --- HEADER TÌM KIẾM ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(240, 240, 240));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        topBar.add(new JLabel("Nhập mã phòng cần xuất hóa đơn: "));
        txtSearchBill = new JTextField(15);
        JButton btnSearch = new JButton("Tìm kiếm");

        btnSearch.addActionListener(e -> loadDataFromDB(txtSearchBill.getText().trim()));
        txtSearchBill.addActionListener(e -> loadDataFromDB(txtSearchBill.getText().trim())); // Enter cũng tìm

        topBar.add(txtSearchBill);
        topBar.add(btnSearch);

        add(topBar, BorderLayout.NORTH);

        // --- BODY HÓA ĐƠN (GIẤY TRẮNG) ---
        JPanel paperPanel = new JPanel(new BorderLayout());
        paperPanel.setBackground(Color.WHITE);
        paperPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. Thông tin khách sạn & Số hóa đơn
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

        // 2. Thông tin khách hàng
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

        lblRoomPrice = new JLabel("Đơn giá: --- /đêm"); // Label này có thể ẩn đi nếu muốn
        lblRoomPrice.setFont(labelFont);

        infoGrid.add(lblCustomerName);
        infoGrid.add(lblRoomNumber);
        infoGrid.add(lblRoomType);
        // infoGrid.add(lblRoomPrice); // Tùy chọn hiển thị

        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setBackground(Color.WHITE);
        topContent.add(headerPanel, BorderLayout.NORTH);
        topContent.add(infoGrid, BorderLayout.CENTER);

        paperPanel.add(topContent, BorderLayout.NORTH);

        // 3. Bảng chi tiết (Table)
        String[] cols = {"STT", "Dịch vụ / Phòng", "Đơn giá", "Số lượng", "Thành tiền"};
        tableModel = new DefaultTableModel(cols, 0);
        tblItems = new JTable(tableModel);
        tblItems.setFillsViewportHeight(true);
        tblItems.setRowHeight(28);
        tblItems.setShowVerticalLines(false);
        tblItems.setGridColor(new Color(230, 230, 230));

        // Căn phải cho cột tiền
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblItems.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblItems.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Số lượng
        tblItems.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(tblItems);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        paperPanel.add(scrollPane, BorderLayout.CENTER);

        // 4. Footer Tổng tiền
        JPanel footerPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        lblTotal = createTotalLabel("Cộng tiền hàng:", "0 đ");
        lblTax = createTotalLabel("Thuế VAT (0%):", "0 đ");
        lblFinalTotal = createTotalLabel("TỔNG THANH TOÁN:", "0 đ");
        lblFinalTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblFinalTotal.setForeground(Color.RED);

        footerPanel.add(lblTotal);
        footerPanel.add(lblTax);
        footerPanel.add(lblFinalTotal);

        paperPanel.add(footerPanel, BorderLayout.SOUTH);
        add(paperPanel, BorderLayout.CENTER);

        // --- BUTTONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setBackground(new Color(240, 240, 240));

        JButton btnPrint = new JButton("🖨 In Hóa Đơn");
        JButton btnClose = new JButton("Đóng");

        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(120, 35));

        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đang gửi lệnh in... (Chức năng mô phỏng)"));
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

    // --- PHẦN QUAN TRỌNG: SỬA LOGIC LOAD DỮ LIỆU ---
    public void loadDataFromDB(String roomId) {
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!");
            return;
        }

        // 1. Lấy thông tin Header (Khách hàng, loại phòng...)
        String[] info = invoiceDAO.getCustomerInfo(roomId); // Hàm này trả về String[]: Tên, SĐT, Phòng, Loại

        if (info != null) {
            lblInvoiceID.setText("HÓA ĐƠN #: INV-" + System.currentTimeMillis() % 10000);
            lblCustomerName.setText("Khách hàng: " + info[0]);
            lblRoomNumber.setText("Phòng số: " + info[2]);
            lblRoomType.setText("Loại phòng: " + info[3]);
            // lblPhone.setText(info[1]); // Nếu muốn hiện SĐT
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách đang ở phòng: " + roomId);
            return;
        }

        // 2. Lấy danh sách chi tiết (Gồm Tiền phòng + Dịch vụ) từ DAO
        List<InvoiceDAO.InvoiceLine> items = invoiceDAO.getInvoiceDetails(roomId);

        tableModel.setRowCount(0); // Xóa bảng cũ
        double grandTotal = 0;
        int stt = 1;

        // Định dạng tiền tệ Việt Nam
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        DecimalFormat dfQty = new DecimalFormat("#,###"); // Định dạng số lượng

        for (InvoiceDAO.InvoiceLine item : items) {
            tableModel.addRow(new Object[]{
                    stt++,
                    item.name, // Tên dịch vụ hoặc "Tiền phòng..."
                    currency.format(item.price),
                    item.quantity + " " + item.unit,
                    currency.format(item.total)
            });
            grandTotal += item.total; // Cộng dồn tổng tiền
        }

        // 3. Cập nhật Footer Tổng tiền
        String totalStr = currency.format(grandTotal);
        lblTotal.setText("Cộng tiền hàng:       " + totalStr);
        lblTax.setText("Thuế VAT (0%):        0 đ"); // Nếu có thuế thì nhân thêm
        lblFinalTotal.setText("TỔNG THANH TOÁN:      " + totalStr);
    }
}