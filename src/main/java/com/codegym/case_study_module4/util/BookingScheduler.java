package com.codegym.case_study_module4.util;

import com.codegym.case_study_module4.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingScheduler {
    private final BookingRepository bookingRepository;

    // Chạy mỗi 1 phút
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void updateExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        int updated = bookingRepository.updateStatusWhenExpired(now);
        System.out.println("Đã cập nhật " + updated + " booking quá hạn lúc " + now);
    }
}
