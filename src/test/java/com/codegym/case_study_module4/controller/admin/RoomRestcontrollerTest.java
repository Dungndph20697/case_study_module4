package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.service.IRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomRestcontrollerTest {

    @Mock
    private IRoomService roomService;

    @InjectMocks
    private RoomRestcontroller roomRestcontroller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRoom_InvalidRoomNumber_ShouldReturnBadRequest() {
        Room room = new Room();
        room.setNumberRoom("ABC123"); // Invalid room number
        room.setPrice(1000000.0);

        ResponseEntity<?> response = roomRestcontroller.addRoom(room);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Số phòng chỉ được chứa chữ số.", response.getBody());
    }

    @Test
    void addRoom_InvalidRoomPrice_ShouldReturnBadRequest() {
        Room room = new Room();
        room.setNumberRoom("101");
        room.setPrice(1000001.0); // Invalid price (not a multiple of 1,000)

        ResponseEntity<?> response = roomRestcontroller.addRoom(room);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Giá phòng phải là bội số của 1.000.", response.getBody());
    }

    @Test
    void addRoom_ValidRoom_ShouldReturnOk() {
        Room room = new Room();
        room.setNumberRoom("101");
        room.setPrice(1000000.0); // Valid price

        when(roomService.save(any(Room.class))).thenReturn(room);

        ResponseEntity<?> response = roomRestcontroller.addRoom(room);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(room, response.getBody());
    }
}
