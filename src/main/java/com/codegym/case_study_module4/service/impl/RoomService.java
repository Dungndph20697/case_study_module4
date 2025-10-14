package com.codegym.case_study_module4.service.impl;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.repository.RoomRepository;
import com.codegym.case_study_module4.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService implements IRoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        Sort sort = Sort.by("id").ascending().descending();
        return roomRepository.findAll(sort);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public void remove(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<Room> findAvailableRooms(LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }
}
