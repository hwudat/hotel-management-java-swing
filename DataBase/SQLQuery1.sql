USE HotelManagement;
GO

-- 1. Thêm dữ liệu bảng Account (Tài khoản đăng nhập)
INSERT INTO Account (username, password, role) VALUES 
('admin', '123456', N'Manager'),
('letan1', '123456', N'Receptionist'),
('letan2', '123456', N'Receptionist');

-- 2. Thêm dữ liệu bảng Employee (Nhân viên)
-- [ĐÃ SỬA]: Thêm cột position và sex
INSERT INTO Employee (full_name, phone, email, salary, position, sex, username) VALUES 
(N'Nguyễn Văn Quản Lý', '0901234567', 'quanly@hotel.com', 20000000, N'Manager', N'Nam', 'admin'),
(N'Trần Thị Lễ Tân', '0912345678', 'letan1@hotel.com', 8000000, N'Receptionist', N'Nữ', 'letan1'),
(N'Lê Văn Trực', '0987654321', 'letan2@hotel.com', 7500000, N'Receptionist', N'Nam', 'letan2');

-- 3. Thêm dữ liệu bảng Customer (Khách hàng)
-- [ĐÃ SỬA]: Thêm cột sex
INSERT INTO Customer (full_name, phone, identity_card, address, sex) VALUES 
(N'Phạm Minh Hàng', '0999888777', '001098000001', N'Hà Nội', N'Nam'),
(N'Nguyễn Thị Lịch', '0988777666', '001099000002', N'Đà Nẵng', N'Nữ'),
(N'John Doe', '0911223344', 'PASS123456', N'USA', N'Nam');

-- 4. Thêm dữ liệu bảng RoomType (Loại phòng)
-- [ĐÃ SỬA]: Thêm cột description
INSERT INTO RoomType (type_name, price_per_night, description) VALUES 
(N'Standard (Tiêu chuẩn)', 500000, N'Phòng cơ bản, 1 giường đôi, đầy đủ tiện nghi.'),
(N'Deluxe (Cao cấp)', 1000000, N'Phòng rộng, view đẹp, nội thất sang trọng.'),
(N'VIP Suite (Thượng hạng)', 2500000, N'Căn hộ cao cấp, có phòng khách riêng và bồn tắm.');

-- 5. Thêm dữ liệu bảng Room (Phòng)
-- [ĐÃ SỬA]: Xóa dấu chấm phẩy thừa ở dòng P202 để chạy một lèo
INSERT INTO Room (room_id, room_number, status, type_id) VALUES 
('P101', '101', N'Available', 1), 
('P102', '102', N'Occupied', 1),  
('P103', '103', N'Available', 2), 
('P201', '201', N'Cleaning', 2),  
('P202', '202', N'Available', 3), 
('P203', '203', N'Available', 1), 
('P301', '301', N'Available', 1),  
('P302', '302', N'Occupied', 2), 
('P303', '303', N'Occupied', 2); 

-- 6. Thêm dữ liệu bảng Service (Dịch vụ)
-- [ĐÃ SỬA]: Thêm cột classify và unit
INSERT INTO Service (service_name, price, classify, unit) VALUES 
(N'Nước ngọt Coca', 15000, N'Nước uống', N'Lon'),
(N'Bia Tiger', 25000, N'Nước uống', N'Lon'),
(N'Giặt ủi', 50000, N'Dịch vụ phòng', N'Lần'),
(N'Ăn sáng Buffet', 150000, N'Đồ ăn', N'Suất'),
(N'Massage thư giãn', 300000, N'Spa & Sức khỏe', N'Giờ');

-- 7. Thêm dữ liệu bảng Booking (Đặt phòng)
-- [ĐÃ SỬA]: Sửa 'R101' thành 'P101' để khớp với bảng Room
INSERT INTO Booking (customer_id, room_id, check_in_date, check_out_date, status, employee_id) VALUES 
(1, 'P101', '2025-10-20 14:00:00', '2025-10-22 12:00:00', N'Checked Out', 2);

-- Booking 2
INSERT INTO Booking (customer_id, room_id, check_in_date, check_out_date, status, employee_id) VALUES 
(2, 'P102', GETDATE(), NULL, N'Checked In', 3);

-- 8. Thêm dữ liệu bảng BookingDetail
INSERT INTO BookingDetail (booking_id, service_id, quantity, total_price) VALUES 
(1, 1, 2, 30000), 
(1, 3, 1, 50000), 
(2, 4, 2, 300000); 

-- 9. Thêm dữ liệu bảng Invoice
INSERT INTO Invoice (booking_id, payment_date, total_amount, payment_method) VALUES 
(1, '2025-10-22 12:15:00', 1080000, N'Tiền mặt');

-- 10. Thêm dữ liệu bảng Feedback
INSERT INTO Feedback (customer_id, rating, comment, created_at) VALUES 
(1, 5, N'Phòng sạch sẽ, nhân viên nhiệt tình, sẽ quay lại!', GETDATE());

INSERT INTO ServiceUsage (room_id, service_id, quantity, total_price, usage_date)
VALUES ('P101', 1, 2, 20000, GETDATE());

INSERT INTO ServiceUsage (room_id, service_id, quantity, total_price, usage_date)
VALUES ('P101', 2, 5, 125000, GETDATE());