package com.codegym.case_study_module4.controller.user;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.service.impl.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Xem chi tiết
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Room room = roomService.findById(id).orElse(null);
        model.addAttribute("room", room);
        return "/user/chi-tiet-phong";
    }

    // Đặt phòng (POST)
    @PostMapping("/book/{id}")
    public String book(@PathVariable Long id, Model model) {
        boolean ok = roomService.book(id);
        model.addAttribute("message", ok ? "Đặt phòng thành công!" : "Không thể đặt phòng (đã thuê hoặc không tồn tại).");
        return "redirect:/user/bang-dieu-khien";
    }

    @GetMapping("/bang-dieu-khien")
    public String listRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        // Nếu người dùng nhập từ khóa tìm kiếm
        if (!keyword.isEmpty()) {
            List<Room> results = roomService.searchRoomsNoPage(keyword);

            if (results.size() == 1) {
                // ✅ Nếu chỉ tìm thấy 1 phòng duy nhất → chuyển sang trang chi tiết
                Room foundRoom = results.get(0);
                model.addAttribute("room", foundRoom);
                return "user/chi-tiet-phong";
            } else {
                // ✅ Nếu có nhiều phòng hoặc không có → hiển thị danh sách có lọc
                Page<Room> roomPage = roomService.searchRooms(keyword, page, size);
                model.addAttribute("rooms", roomPage.getContent());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", roomPage.getTotalPages());
                model.addAttribute("totalRooms", roomPage.getTotalElements());
                model.addAttribute("keyword", keyword);
                return "user/bang-dieu-khien";
            }
        }

        // ✅ Nếu không có tìm kiếm → hiển thị danh sách phòng phân trang bình thường
        Page<Room> roomPage = roomService.getAllRooms(page, size);
        model.addAttribute("rooms", roomPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("totalRooms", roomPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        return "user/bang-dieu-khien";
    }
}
