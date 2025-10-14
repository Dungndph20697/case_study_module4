package com.codegym.case_study_module4.controller.admin;

import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.service.IRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

//    @Autowired
//    private IRoomService roomService;
//
//    @GetMapping(value = "rooms/get-rooms",produces = {MediaType.APPLICATION_JSON_VALUE, "application/xml"})
//    @ResponseBody
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<List<Room>> getAllRooms() {
//        return ResponseEntity.ok(roomService.findAll());
//    }

    @GetMapping("/bang-dieu-khien")
    public String adminDashboard() {
        return "admin/bang-dieu-khien";
    }

    @GetMapping("/quan-ly-phong")
    public String quanLyPhong() {
        return "admin/quan-ly-phong";
    }

    @GetMapping("/quan-ly-dat-phong")
    public String quanLyDatPhong() {
        return "admin/quan-ly-dat-phong";
    }

    @GetMapping("/quan-ly-nguoi-dung")
    public String quanLyNguoiDung() {
        return "admin/quan-ly-nguoi-dung";
    }

    @GetMapping("/trang-thai-phong")
    public String trangThaiPhong() {
        return "admin/trang-thai-phong";
    }

    @GetMapping("/bao-cao")
    public String baoCao() {
        return "admin/bao-cao";
    }

}