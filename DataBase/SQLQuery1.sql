USE HotelManagement;
GO

-- 1. Thêm dữ liệu bảng Account (Tài khoản đăng nhập)
INSERT INTO Account (username, password, role) VALUES 
('admin', '123456', N'Manager'),
('le_tan_1', '123456', N'Receptionist'),
('le_tan_2', '123456', N'Receptionist');

-- 2. Thêm dữ liệu bảng Employee (Nhân viên)
-- Lưu ý: username phải trùng với bảng Account ở trên
INSERT INTO Employee (full_name, phone, email, salary, username) VALUES 
(N'Nguyễn Văn Quản Lý', '0901234567', 'quanly@hotel.com', 20000000, 'admin'),
(N'Trần Thị Lễ Tân', '0912345678', 'letan1@hotel.com', 8000000, 'le_tan_1'),
(N'Lê Văn Trực', '0987654321', 'letan2@hotel.com', 7500000, 'le_tan_2');

-- 3. Thêm dữ liệu bảng Customer (Khách hàng)
INSERT INTO Customer (full_name, phone, identity_card, address) VALUES 
(N'Phạm Khách Hàng', '0999888777', '001098000001', N'Hà Nội'),
(N'Nguyễn Thị Du Lịch', '0988777666', '001099000002', N'Đà Nẵng'),
(N'John Doe', '0911223344', 'PASS123456', N'USA');

-- 4. Thêm dữ liệu bảng RoomType (Loại phòng)
INSERT INTO RoomType (type_name, price_per_night) VALUES 
(N'Standard (Tiêu chuẩn)', 500000),
(N'Deluxe (Cao cấp)', 1000000),
(N'VIP Suite (Thượng hạng)', 2500000);

-- 5. Thêm dữ liệu bảng Room (Phòng)
-- Giả sử ID loại phòng theo thứ tự trên là 1, 2, 3
INSERT INTO Room (room_id, room_number, status, type_id) VALUES 
('R101', '101', N'Available', 1), -- Phòng Standard
('R102', '102', N'Occupied', 1),  -- Phòng Standard đang có khách
('R201', '201', N'Available', 2), -- Phòng Deluxe
('R202', '202', N'Cleaning', 2),  -- Phòng Deluxe đang dọn
('R301', '301', N'Available', 3); -- Phòng VIP

-- 6. Thêm dữ liệu bảng Service (Dịch vụ)
INSERT INTO Service (service_name, price) VALUES 
(N'Nước ngọt Coca', 15000),
(N'Bia Tiger', 25000),
(N'Giặt ủi', 50000),
(N'Ăn sáng Buffet', 150000),
(N'Massage thư giãn', 300000);

-- 7. Thêm dữ liệu bảng Booking (Đặt phòng)
-- Booking 1: Khách hàng 1 đã checkout xong
INSERT INTO Booking (customer_id, room_id, check_in_date, check_out_date, status, employee_id) VALUES 
(1, 'R101', '2023-10-20 14:00:00', '2023-10-22 12:00:00', N'Checked Out', 2);

-- Booking 2: Khách hàng 2 đang ở (chưa checkout)
INSERT INTO Booking (customer_id, room_id, check_in_date, check_out_date, status, employee_id) VALUES 
(2, 'R102', GETDATE(), NULL, N'Checked In', 3);

-- 8. Thêm dữ liệu bảng BookingDetail (Chi tiết sử dụng dịch vụ)
-- Giả sử Booking 1 có ID là 1, Booking 2 có ID là 2
INSERT INTO BookingDetail (booking_id, service_id, quantity, total_price) VALUES 
(1, 1, 2, 30000), -- Khách 1 uống 2 chai Coca (15k * 2)
(1, 3, 1, 50000), -- Khách 1 dùng 1 lần giặt ủi
(2, 4, 2, 300000); -- Khách 2 ăn 2 suất Buffet

-- 9. Thêm dữ liệu bảng Invoice (Hóa đơn)
-- Chỉ tạo hóa đơn cho Booking 1 đã checkout
-- Tiền phòng: 2 đêm * 500k = 1tr. Dịch vụ: 80k. Tổng: 1.080.000
INSERT INTO Invoice (booking_id, payment_date, total_amount, payment_method) VALUES 
(1, '2023-10-22 12:15:00', 1080000, N'Tiền mặt');

-- 10. Thêm dữ liệu bảng Feedback (Phản hồi)
INSERT INTO Feedback (customer_id, rating, comment, created_at) VALUES 
(1, 5, N'Phòng sạch sẽ, nhân viên nhiệt tình, sẽ quay lại!', GETDATE());
