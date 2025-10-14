package com.codegym.case_study_module4.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @GetMapping("/bang-dieu-khien")
    public String staffDashboard() {
        return "staff/bang-dieu-khien";
    }

    @GetMapping("/quan-ly-dat-phong")
    public String staffQuanLyDatPhong() {
        return "admin/quan-ly-dat-phong"; // Dùng lại giao diện admin
    }

    @GetMapping("/trang-thai-phong")
    public String staffTrangThaiPhong() {
        return "admin/trang-thai-phong"; // Dùng lại giao diện admin
    }
}