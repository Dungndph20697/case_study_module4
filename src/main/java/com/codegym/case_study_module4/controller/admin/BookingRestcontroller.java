package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
