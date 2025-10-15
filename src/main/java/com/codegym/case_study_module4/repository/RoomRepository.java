package com.codegym.case_study_module4.repository;

import com.codegym.case_study_module4.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //  Lấy tất cả phòng có ảnh (nếu bạn có quan hệ @OneToMany)
    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.images")
    List<Room> findAllWithImages();

    //  Lấy danh sách phòng có phân trang (không fetch ảnh)
    @Query("SELECT r FROM Room r")
    Page<Room> findAllRooms(Pageable pageable);

    // Tìm kiếm theo số phòng (numberRoom) hoặc loại phòng (typeRoom) - có phân trang
    @Query("SELECT r FROM Room r WHERE LOWER(r.numberRoom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.typeRoom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Room> searchRooms(@Param("keyword") String keyword, Pageable pageable);

    //  Tìm kiếm không phân trang (dùng để xác định nếu chỉ có 1 kết quả)
    @Query("SELECT r FROM Room r WHERE LOWER(r.numberRoom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.typeRoom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Room> searchRoomsNoPage(@Param("keyword") String keyword);
}
