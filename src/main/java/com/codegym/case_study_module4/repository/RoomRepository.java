package com.codegym.case_study_module4.repository;

import com.codegym.case_study_module4.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "SELECT b.room.id FROM Booking b WHERE " +
            "(:checkIn BETWEEN b.checkInDate AND b.checkOutDate OR " +
            ":checkOut BETWEEN b.checkInDate AND b.checkOutDate OR " +
            "b.checkInDate BETWEEN :checkIn AND :checkOut OR " +
            "b.checkOutDate BETWEEN :checkIn AND :checkOut))")
    List<Room> findAvailableRooms(
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );
}
