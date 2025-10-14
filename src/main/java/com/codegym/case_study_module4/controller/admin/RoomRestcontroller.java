package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class RoomRestcontroller {

    @Autowired
    private IRoomService roomService;

    @GetMapping("/rooms/get-rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/rooms/get-rooms/{id}")
    public ResponseEntity<Room> getAllRooms(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findById(id).get());
    }

    @PostMapping("/rooms/add-room")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {
        // số phòng chỉ chứa chữ số
        if (!room.getNumberRoom().matches("\\d+")) {
            return ResponseEntity.badRequest().body("Số phòng chỉ được chứa chữ số.");
        }

        // giá phòng theo định dạng tiền việt
        if (room.getPrice() == null || room.getPrice() <= 0) {
            return ResponseEntity.badRequest().body("Giá phòng phải là số dương và không được để trống.");
        }
        // No need to convert to string, directly validate the numeric value
        if (room.getPrice() % 1000 != 0) {
            return ResponseEntity.badRequest().body("Giá phòng phải là bội số của 1.000.");
        }

        return ResponseEntity.ok(roomService.save(room));
    }

    @DeleteMapping("/rooms/delete-room/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomService.remove(id);
    }

    @PutMapping("/rooms/add-room/{id}")
    public ResponseEntity<?> addRoom(@PathVariable Long id, @RequestBody Room room) {
        // Validate room number (digits only)
        if (!room.getNumberRoom().matches("\\d+")) {
            return ResponseEntity.badRequest().body("Lỗi: Số phòng chỉ được chứa chữ số.");
        }

        // Validate room price (Vietnamese currency format)
        if (room.getPrice() == null || room.getPrice() <= 0) {
            return ResponseEntity.badRequest().body("Lỗi: Giá phòng phải là số dương và không được để trống.");
        }
        if (room.getPrice() % 1000 != 0) {
            return ResponseEntity.badRequest().body("Lỗi: Giá phòng phải là bội số của 1.000.");
        }

        room.setId(id);
        return ResponseEntity.ok(roomService.save(room));
    }

}
