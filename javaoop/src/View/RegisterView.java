package View;

import DAO.AccountDAO;
import models.Account;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class RegisterView extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JPasswordField txtConfirmPass;
    private JComboBox<String> cbRole;
    private JButton btnRegister;
    private JButton btnBack;
    
    private AccountDAO accountDAO;
    private int xMouse, yMouse;

    public RegisterView() {
        accountDAO = new AccountDAO();
        initComponents();
        initEvents();
    }

    private void initComponents() {
        setTitle("Đăng Ký Tài Khoản");
        setSize(450, 550); // Form dọc nhỏ gọn
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBounds(0, 0, 450, 60);
        headerPanel.setLayout(null);
        
        JLabel lbTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbTitle.setForeground(Color.WHITE);
        lbTitle.setBounds(120, 15, 250, 30);
        headerPanel.add(lbTitle);

        // Nút tắt
        JLabel lbClose = new JLabel("X");
        lbClose.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbClose.setForeground(Color.WHITE);
        lbClose.setBounds(410, 15, 30, 30);
        lbClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose(); // Chỉ đóng form đăng ký, không tắt app
            }
        });
        headerPanel.add(lbClose);
        add(headerPanel);

        // --- FORM INPUT ---
        int startY = 80;
        int gap = 70;

        // 1. Username
        JLabel l1 = new JLabel("Tên đăng nhập:");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l1.setBounds(50, startY, 150, 20);
        add(l1);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setBounds(50, startY + 25, 350, 30);
        txtUser.setBorder(new MatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        add(txtUser);

        // 2. Password
        JLabel l2 = new JLabel("Mật khẩu:");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l2.setBounds(50, startY + gap, 150, 20);
        add(l2);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setBounds(50, startY + gap + 25, 350, 30);
        txtPass.setBorder(new MatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        add(txtPass);

        // 3. Confirm Password
        JLabel l3 = new JLabel("Xác nhận mật khẩu:");
        l3.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l3.setBounds(50, startY + gap * 2, 150, 20);
        add(l3);

        txtConfirmPass = new JPasswordField();
        txtConfirmPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtConfirmPass.setBounds(50, startY + gap * 2 + 25, 350, 30);
        txtConfirmPass.setBorder(new MatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        add(txtConfirmPass);

        // 4. Role (Vai trò)
        JLabel l4 = new JLabel("Vai trò:");
        l4.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l4.setBounds(50, startY + gap * 3, 150, 20);
        add(l4);

        // ComboBox cho phép chọn vai trò
        String[] roles = {"Receptionist", "Manager"};
        cbRole = new JComboBox<>(roles);
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbRole.setBounds(50, startY + gap * 3 + 25, 350, 35);
        cbRole.setBackground(Color.WHITE);
        add(cbRole);

        // --- BUTTONS ---
        btnRegister = new JButton("Đăng Ký");
        btnRegister.setBackground(new Color(44, 62, 80));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setBounds(50, 420, 160, 40);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnRegister);

        btnBack = new JButton("Hủy bỏ");
        btnBack.setBackground(new Color(231, 76, 60));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBounds(240, 420, 160, 40);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnBack);

        // Kéo thả cửa sổ
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                setLocation(evt.getXOnScreen() - xMouse, evt.getYOnScreen() - yMouse);
            }
        });
    }

    private void initEvents() {
        // Sự kiện nút Hủy
        btnBack.addActionListener(e -> dispose());

        // Sự kiện nút Đăng Ký
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword());
                String confirm = new String(txtConfirmPass.getPassword());
                String role = cbRole.getSelectedItem().toString();

                // 1. Kiểm tra rỗng
                if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // 2. Kiểm tra mật khẩu xác nhận
                if (!pass.equals(confirm)) {
                    JOptionPane.showMessageDialog(null, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. Kiểm tra user tồn tại (Optional - Nên làm thêm hàm checkExistUser trong DAO)
                Account existingAcc = accountDAO.checkLogin(user, pass); 
                // Lưu ý: đây là cách check lười, đúng ra phải viết hàm checkUserExist riêng
                // Nhưng ở đây ta cứ thử insert, nếu trùng PK nó sẽ văng Exception
                
                Account newAcc = new Account(user, pass, role);

                if (accountDAO.addAccount(newAcc)) {
                    JOptionPane.showMessageDialog(null, "Đăng ký thành công! Hãy đăng nhập ngay.");
                    dispose(); // Đóng form đăng ký
                } else {
                    JOptionPane.showMessageDialog(null, "Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}