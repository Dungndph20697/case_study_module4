package com.codegym.case_study_module4.controller.user;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.service.impl.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final RoomService roomService;

    public UserController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/trang-chu")
    public String userDashboard(Model model) {
        List<Room> rooms = roomService.findAll();
        System.out.println(">>> Tổng số phòng: " + rooms.size());
        model.addAttribute("rooms", rooms);
        return "user/trang-chu";
    }

    @GetMapping("/dat-phong")
    public String userDatPhong() {
        return "user/dat-phong";
    }
}