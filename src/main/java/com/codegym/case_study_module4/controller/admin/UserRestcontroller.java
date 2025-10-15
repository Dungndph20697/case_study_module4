package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class UserRestcontroller {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/users/get-users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{id}/bookings")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long id) {
        List<Booking> bookings = bookingService.findByUserId(id);
        return ResponseEntity.ok(bookings);
    }

    // hàm xác thực các trường
    @PostMapping("/users/add-user")
    public ResponseEntity<?> addUser(@RequestBody Users user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }
        // check email có trùng không
        if (userService.existsByEmailIgnoreCase(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email bị trùng");
        }
        // check cccd
        if (user.getCitizenIdNumber() != null && !user.getCitizenIdNumber().trim().isEmpty()) {
            if (userService.existsByCitizenIdNumber(user.getCitizenIdNumber())) {
                return ResponseEntity.badRequest().body("Căn cước/CMND đã tồn tại");
            }
        }
        //
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/users/add-user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Users user) {
        if (user.getEmail() != null) {
            Optional<Users> byEmail = userService.findByEmailIgnoreCase(user.getEmail());
            if (byEmail.isPresent() && !byEmail.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("Email already exists for another user");
            }
        }
        if (user.getCitizenIdNumber() != null && !user.getCitizenIdNumber().trim().isEmpty()) {
            Optional<Users> byCid = userService.findByCitizenIdNumber(user.getCitizenIdNumber());
            if (byCid.isPresent() && !byCid.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("Căn cước/CMND đã tồn tại cho người khác");
            }
        }

        return userService.findById(id).map(existing -> {
            existing.setUsername(user.getUsername());
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            existing.setRole(user.getRole());
            existing.setEmail(user.getEmail());
            existing.setPhoneNumber(user.getPhoneNumber());
            existing.setStatus(user.getStatus());
            existing.setCitizenIdNumber(user.getCitizenIdNumber());
            Users saved = userService.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }


    // phương thức đang được cân nhắc
//    @DeleteMapping("/users/delete-user/{id}")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        return userService.findById(id).map(existing -> {
//            existing.setStatus(0); // 0 = inactive/locked
//            userService.save(existing);
//            return ResponseEntity.ok("User marked as inactive");
//        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
//    }


    // phương thức đang được cân nhắc
//    @DeleteMapping("/users/force-delete/{id}")
//    @Transactional
//    public ResponseEntity<?> forceDeleteUser(@PathVariable Long id) {
//        try {
//            return userService.findById(id).map(existing -> {
//                existing.setStatus(0);
//                userService.save(existing);
//                return ResponseEntity.ok("User marked as inactive (force)");
//            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
//        } catch (DataIntegrityViolationException ex) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Không thể xóa do ràng buộc dữ liệu");
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa người dùng");
//        }
//    }

}
