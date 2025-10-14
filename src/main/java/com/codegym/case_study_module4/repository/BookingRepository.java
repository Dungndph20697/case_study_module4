package com.codegym.case_study_module4.repository;

import com.codegym.case_study_module4.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Modifying
    @Query("delete from Booking b where b.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
