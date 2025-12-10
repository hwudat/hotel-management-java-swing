package Service;

import DAO.AccountDAO;
import models.Account;

public class AuthService {
    private AccountDAO accountDAO;

    public AuthService() {
        this.accountDAO = new AccountDAO();
    }

    public Account login(String username, String password) {
        // 1. Gọi DAO lấy thông tin user từ DB
        Account acc = accountDAO.getAccountByUsername(username);

        // 2. Kiểm tra mật khẩu (Logic nghiệp vụ)
        if (acc != null && acc.getPassword().equals(password)) {
            return acc; // Đăng nhập thành công
        }
        return null; // Sai pass hoặc không tồn tại user
    }
}