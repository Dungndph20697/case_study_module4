SELECT * FROM quanlydatphong.rooms;

CREATE TABLE room_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(255),
    room_id BIGINT,
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

INSERT INTO rooms (number_room, type_room, price, status_room, description)
values('P101', 'Phòng Thường', 300000, 'AVAILABLE', 'Phòng thoáng mát, đầy đủ tiện nghi cơ bản.');
use quanlydatphong;
INSERT INTO images(url, room_id) 
VALUES('/images/thuong1.png', 1),
('/images/thuong2.png', 1),
('/images/thuong3.png', 1),
('/images/thuong4.png', 1),
('/images/thuong5.png', 1),
('/images/thuong6.png', 1);

INSERT INTO rooms (number_room, type_room, price, status_room, description)
values('P105', 'Phòng Đôi', 500000, 'AVAILABLE', 'Phòng thoáng mát, đầy đủ tiện nghi cơ bản, giường đôi, tiện ích.'),
('P106', 'Phòng VIP', 1000000, 'AVAILABLE', 'Phòng view biển, đâu đủ tiển nghi, có bồn tắm, phòng xông hơi, tiện ích full, ăn sáng, trưa, tối'),
('P107', 'Phòng Tổng Thống', 3000000, 'AVAILABLE', 'Phòng đặc biêt, hoàng giá full dịch vụ, tiện nghi, quản gia riêng 24/7');

INSERT INTO images(url, room_id) 
VALUES('/images/doi7.png', 5),
('/images/doi8.png', 5),
('/images/doi9.png', 5),
('/images/doi10.png', 5),
('/images/doi11.png', 5);

