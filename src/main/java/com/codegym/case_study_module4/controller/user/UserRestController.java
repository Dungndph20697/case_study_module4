package com.codegym.case_study_module4.controller.user;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserRestController {

    @Autowired
    private IRoomService roomService;

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/get-rooms")
    public ResponseEntity<List<Room>> userDatPhong() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/get-rooms-available")
    public ResponseEntity<List<Room>> userDatPhong(@RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
                                                   @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        return ResponseEntity.ok(roomService.findAvailableRooms(checkIn, checkOut));
    }

    @PostMapping("/dat-phong")
    public ResponseEntity<Booking> datPhong(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();
        Long userId = user.getId();
//        Optional<Room> room = roomService.findById(booking.getRoom().getId());
//        if (room.isPresent()) {
//            Room room1 = room.get();
//            room1.setStatusRoom(1);
//            roomService.save(room1);
//        }
        booking.setUser(Users.builder().id(userId).build());
        booking.setStatus(0);
        booking.setCode(bookingService.generateBookingCode());
        return ResponseEntity.ok(bookingService.save(booking));
    }

}
