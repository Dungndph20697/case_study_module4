package com.codegym.case_study_module4.service;

import com.codegym.case_study_module4.model.Booking;

import java.util.List;

public interface IBookingService extends IGenerateService<Booking> {
    List<Booking> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
