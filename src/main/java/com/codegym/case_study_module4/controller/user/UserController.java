package com.codegym.case_study_module4.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/bang-dieu-khien")
    public String userDashboard() {
        return "user/bang-dieu-khien";
    }

    @GetMapping("/user/dat-phong")
    public String userDatPhong() {
        return "user/dat-phong";
    }
}