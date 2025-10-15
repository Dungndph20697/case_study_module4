package com.codegym.case_study_module4.service.impl;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.repository.BookingRepository;
import com.codegym.case_study_module4.service.IBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public List<Booking> findAll() {
        Sort sort = Sort.by("id").descending();
        return bookingRepository.findAll(sort);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void remove(Long id) {
        bookingRepository.deleteById(id);
    }


    @Override
    public String generateBookingCode() {
        long count = bookingRepository.count() + 1;
        return String.format("B%05d", count);
    }



    @Override
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        bookingRepository.deleteByUserId(userId);
    }

}
