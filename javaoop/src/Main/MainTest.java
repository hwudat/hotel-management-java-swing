package Main;

import DAO.*;
import models.*;
import java.util.Date;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("====== BẮT ĐẦU TEST HỆ THỐNG KHÁCH SẠN ======");

        // --- 1. TEST EMPLOYEE DAO (Thêm nhân viên mới) ---
        System.out.println("\n[1] Đang test thêm Nhân viên...");
        EmployeeDAO empDAO = new EmployeeDAO();
        // Giả sử tài khoản 'staff' đã có trong bảng Account (từ script SQL cũ)
        Receptionist newStaff = new Receptionist("NV01", "Lễ Tân Xinh Đẹp", "0909123456", new Account("staff", "123", "Receptionist"));

        // Lưu ý: Cần đảm bảo username 'staff' đã tồn tại trong DB, nếu chưa có sẽ lỗi khóa ngoại
        try {
            boolean isAdded = empDAO.addEmployee(newStaff, "staff");
            if (isAdded) {
                System.out.println("-> Thêm nhân viên thành công!");
            } else {
                System.out.println("-> Nhân viên đã tồn tại hoặc lỗi.");
            }
        } catch (Exception e) {
            System.out.println("-> Lỗi: Có thể do tài khoản 'staff' chưa có trong DB.");
        }


        // --- 2. TEST CUSTOMER DAO (Thêm khách hàng) ---
        System.out.println("\n[2] Đang test thêm Khách hàng...");
        CustomerDAO custDAO = new CustomerDAO();
        Customer khach = new Customer("KH001", "Nguyễn Văn Test", "0912345678", "00123456789", "Hà Nội");

        if (custDAO.addCustomer(khach)) {
            System.out.println("-> Thêm khách hàng thành công: " + khach.getNameCustomer());
        } else {
            System.out.println("-> Khách hàng có thể đã tồn tại.");
        }


        // --- 3. TEST BOOKING DAO (Đặt phòng) ---
        System.out.println("\n[3] Đang test Đặt phòng...");
        BookingDAO bookingDAO = new BookingDAO();
        RoomDAO roomDAO = new RoomDAO();

        // Lấy phòng R101 (Đảm bảo phòng này đã có trong DB và Status='Available')
        Room room = new Room("R101", null, "trống"); // Chỉ cần ID để đặt

        // Tạo booking: Mã B001, Khách KH001, Phòng R101
        Date today = new Date();
        Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24)); // +1 ngày

        RoomBooking booking = new RoomBooking("B001", room, khach, today, tomorrow);

        if (bookingDAO.addBooking(booking)) {
            System.out.println("-> Đặt phòng thành công! Phòng R101 đã chuyển sang Occupied.");
        } else {
            System.out.println("-> Đặt phòng thất bại (Có thể trùng mã B001 hoặc phòng đang bận).");
        }


        // --- 4. TEST BOOKING DETAIL DAO (Gọi dịch vụ) ---
        System.out.println("\n[4] Đang test Gọi dịch vụ (Booking Detail)...");
        BookingDetailDAO detailDAO = new BookingDetailDAO();

        // Khách gọi 2 chai Pepsi (S01) vào phòng (Booking B001)
        // Giá 20000 là giá tại thời điểm đặt
        if (detailDAO.addServiceToBooking("B001", "S01", 2, 20000)) {
            System.out.println("-> Đã thêm 2 Pepsi vào hóa đơn phòng B001.");
        } else {
            System.out.println("-> Thêm dịch vụ thất bại (Kiểm tra mã S01 có chưa).");
        }

        // Test tính tổng tiền dịch vụ
        double totalService = detailDAO.getTotalServicePrice("B001");
        System.out.println("-> Tổng tiền dịch vụ hiện tại của B001 là: " + totalService + " VND");


        // --- 5. TEST FEEDBACK DAO (Gửi phản hồi) ---
        System.out.println("\n[5] Đang test Gửi phản hồi...");
        FeedbackDAO fbDAO = new FeedbackDAO();

        if (fbDAO.sendFeedback("KH001", "Phòng sạch, nhân viên thân thiện!", 5)) {
            System.out.println("-> Đã gửi phản hồi 5 sao.");
        }

        System.out.println("\n--- DANH SÁCH PHẢN HỒI ---");
        List<String> feedbacks = fbDAO.getAllFeedbacks();
        for (String fb : feedbacks) {
            System.out.println(fb);
        }

        System.out.println("\n====== KẾT THÚC TEST ======");
    }
}