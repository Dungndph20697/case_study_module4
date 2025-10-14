package com.codegym.case_study_module4.controller.user;


import com.codegym.case_study_module4.dto.UserProfileDto;
import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.codegym.case_study_module4.service.IRoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @GetMapping("/bang-dieu-khien")
    public String userDashboard() {
        return "user/bang-dieu-khien";
    }

    @GetMapping("/dat-phong")
    public String userDatPhong() {
        return "user/dat-phong";
    }

    @GetMapping("/thong-tin")
    public String thongTin(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Optional<Users> opt = userService.findByEmailIgnoreCase(principal.getName());
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
            return "redirect:/";
        }
        Users user = opt.get();
        // Populate DTO
        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCitizenIdNumber(user.getCitizenIdNumber());
        model.addAttribute("profile", dto);
        return "user/thong-tin";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("profile") UserProfileDto profileDto,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes,
                         HttpSession session,
                         Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Users> opt = userService.findByEmailIgnoreCase(principal.getName());
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
            return "redirect:/";
        }

        Users user = opt.get();

        // Validate input
        if (bindingResult.hasErrors()) {
            // return form with errors; keep profile model attribute
            model.addAttribute("profile", profileDto);
            return "user/thong-tin";
        }

        // CCCD: kiểm tra tồn tại (nếu thay đổi)
        String newCitizen = profileDto.getCitizenIdNumber();
        if (newCitizen != null && !newCitizen.equals(user.getCitizenIdNumber())) {
            if (userService.existsByCitizenIdNumber(newCitizen)) {
                redirectAttributes.addFlashAttribute("error", "CCCD đã tồn tại");
                return "redirect:/user/thong-tin";
            }
            user.setCitizenIdNumber(newCitizen);
        }

        // Các trường khác cho phép cập nhật (không chạm password/role/email)
        user.setUsername(profileDto.getUsername());
        user.setPhoneNumber(profileDto.getPhoneNumber());

        userService.save(user);

        // Nếu có booking tạm trong session thì hoàn tất lưu booking gắn user
        Object pending = session.getAttribute("PENDING_BOOKING");
        if (pending instanceof Map) {
            try {
                Map<String, String> payload = (Map<String, String>) pending;
                Booking b = new Booking();
                // generate simple code
                b.setCode("BK-" + System.currentTimeMillis());
                String ngayDen = payload.get("ngayDen");
                String ngayDi = payload.get("ngayDi");
                if (ngayDen != null && ngayDi != null) {
                    LocalDate d1 = LocalDate.parse(ngayDen);
                    LocalDate d2 = LocalDate.parse(ngayDi);
                    b.setCheckInDate(d1.atStartOfDay());
                    b.setCheckOutDate(d2.atStartOfDay());
                }
                b.setStatus(0);
                b.setUser(user);
                bookingService.save(b);
                session.removeAttribute("PENDING_BOOKING");
                redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công và hoàn tất đặt phòng");
                return "redirect:/user/bang-dieu-khien";
            } catch (Exception ex) {
                // ignore and continue
            }
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công");
        return "redirect:/user/thong-tin";
    }

    @PostMapping("/dat-phong")
    public String postDatPhong(@RequestParam("ngayDen") String ngayDen,
                               @RequestParam("ngayDi") String ngayDi,
                               @RequestParam(value = "loaiPhong", required = false) String loaiPhong,
                               @RequestParam(value = "ghiChu", required = false) String ghiChu,
                               Principal principal,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        Optional<Users> opt = userService.findByEmailIgnoreCase(principal.getName());
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
            return "redirect:/";
        }
        Users user = opt.get();
        boolean phoneMissing = user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty();
        boolean citizenMissing = user.getCitizenIdNumber() == null || user.getCitizenIdNumber().trim().isEmpty();

        if (phoneMissing || citizenMissing) {
            // Lưu payload tạm vào session để hoàn tất sau khi user nhập thông tin
            session.setAttribute("PENDING_BOOKING", Map.of(
                    "ngayDen", ngayDen,
                    "ngayDi", ngayDi,
                    "loaiPhong", loaiPhong == null ? "" : loaiPhong,
                    "ghiChu", ghiChu == null ? "" : ghiChu
            ));
            redirectAttributes.addFlashAttribute("error", "Bạn cần hoàn thành thông tin cá nhân trước khi đặt phòng.");
            return "redirect:/user/thong-tin";
        }

        // Người dùng đã có thông tin -> tạo booking ngay
        Booking b = new Booking();
        b.setCode("BK-" + System.currentTimeMillis());
        LocalDate d1 = LocalDate.parse(ngayDen);
        LocalDate d2 = LocalDate.parse(ngayDi);
        b.setCheckInDate(d1.atStartOfDay());
        b.setCheckOutDate(d2.atStartOfDay());
        b.setStatus(0);
        b.setUser(user);
        // room not set (form currently does not select specific room)
        bookingService.save(b);

        redirectAttributes.addFlashAttribute("success", "Đặt phòng thành công");
        return "redirect:/user/bang-dieu-khien";
    }

}