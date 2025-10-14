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

    @PostMapping("/rooms/add-room")
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.save(room));
    }

    @DeleteMapping("/rooms/delete-room/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomService.remove(id);
    }

    @PutMapping("/rooms/add-room/{id}")
    public ResponseEntity<Room> addRoom(@PathVariable Long id,@RequestBody Room room) {
        room.setId(id);
        return ResponseEntity.ok(roomService.save(room));
    }

}
