package com.codegym.case_study_module4.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin/bang-dieu-khien")
    public String adminDashboard() {
        return "admin/bang-dieu-khien";
    }

    @GetMapping("/admin/quan-ly-phong")
    public String quanLyPhong() {
        return "admin/quan-ly-phong";
    }

    @GetMapping("/admin/quan-ly-dat-phong")
    public String quanLyDatPhong() {
        return "admin/quan-ly-dat-phong";
    }

    @GetMapping("/admin/quan-ly-nguoi-dung")
    public String quanLyNguoiDung() {
        return "admin/quan-ly-nguoi-dung";
    }

    @GetMapping("/admin/trang-thai-phong")
    public String trangThaiPhong() {
        return "admin/trang-thai-phong";
    }

    @GetMapping("/admin/bao-cao")
    public String baoCao() {
        return "admin/bao-cao";
    }
}