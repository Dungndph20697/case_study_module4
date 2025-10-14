package com.codegym.case_study_module4.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/bang-dieu-khien")
    public String userDashboard() {
        return "user/bang-dieu-khien";
    }

    @GetMapping("/dat-phong")
    public String userDatPhong() {
        return "user/dat-phong";
    }
}