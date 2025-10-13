package com.codegym.case_study_module4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "auth/trang-dang-nhap";
    }
}
