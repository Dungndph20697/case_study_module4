package com.codegym.case_study_module4.service.impl;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.repository.RoomRepository;
import com.codegym.case_study_module4.service.IRoomService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;

    //  Lấy toàn bộ danh sách phòng (kèm ảnh)
    @Override
    public List<Room> findAll() {

        return roomRepository.findAllWithImages();

    }

    //  Tìm phòng theo ID
    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    //  Lưu hoặc cập nhật phòng
    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    //  Xóa phòng theo ID
    @Override
    public void remove(Long id) {
        roomRepository.deleteById(id);
    }


    //  Đặt phòng (chuyển trạng thái)
    public boolean book(Long id) {
        Optional<Room> roomOpt = findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            if (room.getStatusRoom() == Room.Status.AVAILABLE) {
                room.setStatusRoom(Room.Status.BOOKED);
                roomRepository.save(room);
                return true;
            }
        }
        return false;
    }

    //  Lấy tất cả phòng có phân trang
    public Page<Room> getAllRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.findAllRooms(pageable);
    }

    //  Tìm kiếm phòng có phân trang
    public Page<Room> searchRooms(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roomRepository.searchRooms(keyword, pageable);
    }

    //  Tìm kiếm không phân trang (để kiểm tra nếu chỉ có 1 kết quả)
    public List<Room> searchRoomsNoPage(String keyword) {
        return roomRepository.searchRoomsNoPage(keyword);

    @Override
    public List<Room> findAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);

    }
}
