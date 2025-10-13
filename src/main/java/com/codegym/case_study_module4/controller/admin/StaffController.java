package com.codegym.case_study_module4.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    @GetMapping("/staff/bang-dieu-khien")
    public String staffDashboard() {
        return "staff/bang-dieu-khien";
    }

    @GetMapping("/staff/quan-ly-dat-phong")
    public String staffQuanLyDatPhong() {
        return "admin/quan-ly-dat-phong"; // Dùng lại giao diện admin
    }

    @GetMapping("/staff/trang-thai-phong")
    public String staffTrangThaiPhong() {
        return "admin/trang-thai-phong"; // Dùng lại giao diện admin
    }
}