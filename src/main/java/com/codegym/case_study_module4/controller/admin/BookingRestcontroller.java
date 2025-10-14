package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.service.IBookingService;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class BookingRestcontroller {

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/bookings/get-bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/bookings/get-booking/{id}")
    public ResponseEntity<Booking> getAllBookings(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.findById(id);
        return ResponseEntity.ok(booking.get());
    }

    @PutMapping("/bookings/change-staus/{id}")
    public ResponseEntity<Booking> getAllBookings(@PathVariable Long id, @RequestParam("status") Integer status) {
        Optional<Booking> booking = bookingService.findById(id);
        booking.get().setStatus(status);
        booking.get().setId(id);
        return ResponseEntity.ok(bookingService.save(booking.get()));
    }
}
