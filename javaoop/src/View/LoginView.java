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

public class LoginView extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblRegister; // Thêm nhãn đăng ký
    
    private AccountDAO accountDAO; 
    private int xMouse, yMouse;

    public LoginView() {
        accountDAO = new AccountDAO(); 
        initComponents();
        initEvents();
    }

    private void initComponents() {
        // --- 1. SETUP FRAME ---
        setTitle("Đăng nhập hệ thống");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- 2. PANEL TRÁI ---
        JPanel panelLeft = new JPanel();
        panelLeft.setBackground(new Color(44, 62, 80)); 
        panelLeft.setBounds(0, 0, 400, 500);
        panelLeft.setLayout(null);

        JLabel lbTitle1 = new JLabel("HOTEL");
        lbTitle1.setForeground(Color.WHITE);
        lbTitle1.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle1.setBounds(40, 50, 200, 40);
        panelLeft.add(lbTitle1);

        JLabel lbTitle2 = new JLabel("MANAGER");
        lbTitle2.setForeground(Color.WHITE);
        lbTitle2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lbTitle2.setBounds(40, 90, 300, 50);
        panelLeft.add(lbTitle2);

        JLabel lbDesc = new JLabel("<html>Hệ thống quản lý khách sạn<br>Chuyên nghiệp - Hiệu quả</html>");
        lbDesc.setForeground(new Color(200, 200, 200));
        lbDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbDesc.setBounds(40, 160, 300, 50);
        panelLeft.add(lbDesc);
        add(panelLeft);

        // --- 3. PANEL PHẢI ---
        JPanel panelRight = new JPanel();
        panelRight.setBackground(Color.WHITE);
        panelRight.setBounds(400, 0, 450, 500);
        panelRight.setLayout(null);

        // Nút tắt (X)
        JLabel lbClose = new JLabel("X");
        lbClose.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbClose.setForeground(Color.GRAY);
        lbClose.setBounds(420, 10, 30, 30);
        lbClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbClose.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });
        panelRight.add(lbClose);

        JLabel lbLoginHeader = new JLabel("ĐĂNG NHẬP");
        lbLoginHeader.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lbLoginHeader.setForeground(new Color(44, 62, 80));
        lbLoginHeader.setBounds(100, 60, 250, 50);
        panelRight.add(lbLoginHeader);

        // Username
        JLabel l1 = new JLabel("Tên đăng nhập");
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l1.setForeground(Color.GRAY);
        l1.setBounds(50, 140, 150, 20);
        panelRight.add(l1);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setBounds(50, 165, 340, 30);
        txtUser.setBorder(new MatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        panelRight.add(txtUser);

        // Password
        JLabel l2 = new JLabel("Mật khẩu");
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l2.setForeground(Color.GRAY);
        l2.setBounds(50, 220, 150, 20);
        panelRight.add(l2);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setBounds(50, 245, 340, 30);
        txtPass.setBorder(new MatteBorder(0, 0, 2, 0, new Color(44, 62, 80)));
        panelRight.add(txtPass);

        // Nút Login
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(44, 62, 80));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBounds(50, 320, 160, 40);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelRight.add(btnLogin);

        // Nút Thoát
        btnExit = new JButton("Thoát");
        btnExit.setBackground(new Color(231, 76, 60)); // Màu đỏ
        btnExit.setForeground(Color.WHITE);
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnExit.setBounds(230, 320, 160, 40);
        btnExit.setBorderPainted(false);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelRight.add(btnExit);

        // --- PHẦN MỚI THÊM: LINK ĐĂNG KÝ ---
        lblRegister = new JLabel("<html>Bạn chưa có tài khoản? <b>Đăng ký ngay</b></html>");
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRegister.setForeground(new Color(44, 62, 80));
        lblRegister.setBounds(120, 400, 250, 30);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelRight.add(lblRegister);

        add(panelRight);

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
        
        // --- SỰ KIỆN CLICK ĐĂNG KÝ ---
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Mở form đăng ký
                new RegisterView().setVisible(true);
            }
        });

        // Sự kiện nút Đăng Nhập
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText();
                String password = new String(txtPass.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                try {
                    Account acc = accountDAO.checkLogin(username, password);

                    if (acc != null) {
                        JOptionPane.showMessageDialog(null, "Đăng nhập thành công!\nXin chào: " + acc.getRole());
                        new DashboardView().setVisible(true);
                        dispose(); 
                    } else {
                        JOptionPane.showMessageDialog(null, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu!");
                }
            }
        });

        btnExit.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}