package com.codegym.case_study_module4.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    @GetMapping()
    public String showDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/quan-ly-phong")
    public String showQuanLyPhong() {
        return "admin/quanlyphong";
    }
}
