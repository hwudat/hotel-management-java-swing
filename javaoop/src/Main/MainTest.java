package Main;

import DAO.*;
import models.*;
import java.util.Date;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("====== BẮT ĐẦU TEST HỆ THỐNG KHÁCH SẠN (NEW DB) ======");

        String roomID = "101"; 
        int serviceID = 1; 

        System.out.println("\n[1] Đang test thêm Nhân viên...");
        EmployeeDAO empDAO = new EmployeeDAO();
        
        Employee newStaff = new Employee(0, "Trần Văn Test", "Lễ Tân", "Nam", "0909123456", "staff@hotel.com", 8000000);

        if (empDAO.addEmployee(newStaff)) {
            System.out.println("-> [OK] Thêm nhân viên thành công!");
        } else {
            System.out.println("-> [FAIL] Lỗi khi thêm nhân viên.");
        }

        System.out.println("\n[2] Đang test Quy trình Đặt phòng...");
        BookingDAO bookingDAO = new BookingDAO();

        String cusName = "Nguyễn Thị Khách";
        String cusPhone = "0912345678";
        String cusCCCD = "001099123456";
        
        int customerId = bookingDAO.findOrCreateCustomer(cusName, cusPhone, cusCCCD);
        System.out.println("-> ID Khách hàng (Tìm/Tạo): " + customerId);

        if (customerId != -1) {
            
            Date checkIn = new Date();
            Date checkOut = new Date(checkIn.getTime() + (24 * 60 * 60 * 1000)); 
            double deposit = 500000;

            boolean isBooked = bookingDAO.addBooking(customerId, roomID, checkIn, checkOut, deposit);
            
            if (isBooked) {
                System.out.println("-> [OK] Đặt phòng " + roomID + " thành công!");
            } else {
                System.out.println("-> [FAIL] Đặt phòng thất bại (Kiểm tra lại Mã phòng trong DB).");
            }
        }

        System.out.println("\n[3] Đang test Gọi dịch vụ...");
        ServiceDAO serviceDAO = new ServiceDAO();

        int qty = 2;
        double price = 10000;
        double total = price * qty;

        boolean isServiceAdded = serviceDAO.addServiceUsage(roomID, serviceID, qty, total);
        
        if (isServiceAdded) {
            System.out.println("-> [OK] Đã thêm dịch vụ vào phòng " + roomID);
        } else {
            System.out.println("-> [FAIL] Thêm dịch vụ thất bại (Check khóa ngoại Room hoặc Service).");
        }

        System.out.println("\n[4] Kiểm tra dữ liệu Báo cáo...");
        ReportDAO reportDAO = new ReportDAO();
        System.out.println("-> Số khách Check-in hôm nay: " + reportDAO.getTodayCheckInCount());
        System.out.println("-> Số phòng đang có khách: " + reportDAO.getCurrentOccupiedCount());

        System.out.println("\n====== KẾT THÚC TEST ======");
    }
}