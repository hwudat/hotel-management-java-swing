package Service;

import DAO.AccountDAO;
import models.Account;

public class AuthService {
    private AccountDAO accountDAO;

    public AuthService() {
        this.accountDAO = new AccountDAO();
    }

    public Account login(String username, String password) {
        
        Account acc = accountDAO.getAccountByUsername(username);

        if (acc != null && acc.getPassword().equals(password)) {
            return acc; 
        }
        return null;
    }
}