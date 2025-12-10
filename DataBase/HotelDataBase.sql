USE HotelManagement;
GO

CREATE TABLE Account (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role NVARCHAR(50) NOT NULL -- 'Manager', 'Receptionist'
);

CREATE TABLE Employee (
    employee_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    salary DECIMAL(18, 2),
    username VARCHAR(50) UNIQUE,
    FOREIGN KEY (username) REFERENCES Account(username)
);

CREATE TABLE Customer (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE,
    identity_card VARCHAR(20),
    address NVARCHAR(200)
);

CREATE TABLE RoomType (
    type_id INT IDENTITY(1,1) PRIMARY KEY,
    type_name NVARCHAR(50) NOT NULL,
    price_per_night DECIMAL(18, 2) NOT NULL
);

CREATE TABLE Room (
    room_id VARCHAR(10) PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL,
    status NVARCHAR(20) DEFAULT 'Available',
    type_id INT,
    FOREIGN KEY (type_id) REFERENCES RoomType(type_id)
);

CREATE TABLE Service (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    service_name NVARCHAR(100) NOT NULL,
    price DECIMAL(18, 2) NOT NULL
);

CREATE TABLE Booking (
    booking_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT,
    room_id VARCHAR(10),
    check_in_date DATETIME DEFAULT GETDATE(),
    check_out_date DATETIME,
    status NVARCHAR(20),
    employee_id INT,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (room_id) REFERENCES Room(room_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE BookingDetail (
    detail_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT,
    service_id INT,
    quantity INT DEFAULT 1,
    total_price DECIMAL(18, 2),
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
    FOREIGN KEY (service_id) REFERENCES Service(service_id)
);

CREATE TABLE Invoice (
    invoice_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT UNIQUE,
    payment_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(18, 2),
    payment_method NVARCHAR(50),
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id)
);

CREATE TABLE Feedback (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

