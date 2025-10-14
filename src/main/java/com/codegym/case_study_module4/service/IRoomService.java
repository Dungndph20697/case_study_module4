package com.codegym.case_study_module4.service;

import com.codegym.case_study_module4.model.Room;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IRoomService extends IGenerateService<Room> {
    List<Room> findAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut);
}
