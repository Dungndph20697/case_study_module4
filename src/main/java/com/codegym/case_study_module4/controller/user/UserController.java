package com.codegym.case_study_module4.controller.user;


import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/bang-dieu-khien")
    public String userDashboard() {
        logger.debug("Entered userDashboard() -> returning user/bang-dieu-khien");
        return "user/bang-dieu-khien";
    }

    @GetMapping("/lich-su")
    public String userLichSu() {
        return "user/lich-su";
    }

    @GetMapping("/dat-phong")
    public String userDatPhong() {
        logger.debug("Entered userDatPhong() -> returning user/dat-phong");
        return "user/dat-phong";
    }

    @InitBinder("userForm")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "password", "role", "status", "email");
    }

    private String getPrincipalEmail(Principal principal) {
        if (principal == null) return null;
        try {
            // Prefer SecurityContextHolder to be robust
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                Object p = auth.getPrincipal();
                if (p instanceof UserDetails) {
                    return ((UserDetails) p).getUsername();
                }
                // fall back to Principal.getName()
            }
        } catch (Exception ex) {
            logger.debug("Failed to extract principal from SecurityContext: {}", ex.getMessage());
        }
        return principal.getName();
    }

    @GetMapping("/thong-tin")
    public String thongTin(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        logger.debug("Entered thongTin() with principal={}", principal == null ? "<null>" : principal.getName());
        if (principal == null) {
            logger.debug("Principal is null, redirecting to /login");
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để truy cập thông tin cá nhân");
            return "redirect:/login";
        }
        String email = getPrincipalEmail(principal);
        Optional<Users> opt = userService.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            // Previously redirected to '/', which maps to dashboard via HomeController; that's confusing.
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại. Vui lòng đăng nhập lại.");
            logger.debug("User not found for principal={}, redirecting to /login", email);
            return "redirect:/login";
        }
        Users user = opt.get();
        model.addAttribute("userForm", user);
        logger.debug("Rendering user/thong-tin for user id={}", user.getId());
        return "user/thong-tin";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute("userForm") Users userForm,
                         BindingResult bindingResult,
                         Principal principal,
                         RedirectAttributes redirectAttributes,
                         HttpSession session,
                         Model model,
                         @org.springframework.web.bind.annotation.RequestParam(value = "password", required = false) String newPassword,
                         @org.springframework.web.bind.annotation.RequestParam(value = "currentPassword", required = false) String currentPassword,
                         @org.springframework.web.bind.annotation.RequestParam(value = "confirmPassword", required = false) String confirmPassword) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = getPrincipalEmail(principal);
        Optional<Users> opt = userService.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại. Vui lòng đăng nhập lại.");
            logger.debug("User not found in update() for principal={}, redirecting to /login", email);
            return "redirect:/login";
        }

        Users user = opt.get();

        // Validate input
        if (bindingResult.hasErrors()) {
            // return form with errors; keep userForm model attribute
            model.addAttribute("userForm", userForm);
            return "user/thong-tin";
        }

        // CCCD: kiểm tra tồn tại (nếu thay đổi)
        String newCitizen = userForm.getCitizenIdNumber();
        if (newCitizen != null && !newCitizen.equals(user.getCitizenIdNumber())) {
            if (userService.existsByCitizenIdNumber(newCitizen)) {
                redirectAttributes.addFlashAttribute("error", "CCCD đã tồn tại");
                return "redirect:/user/thong-tin";
            }
            user.setCitizenIdNumber(newCitizen);
        }

        // Các trường khác cho phép cập nhật (không chạm password/role/email)
        user.setUsername(userForm.getUsername());
        user.setPhoneNumber(userForm.getPhoneNumber());

        // If user attempts to change password (currentPassword provided), validate and update
        if (currentPassword != null && !currentPassword.trim().isEmpty()) {
            // Validate new password presence
            if (newPassword == null || newPassword.trim().isEmpty()) {
                model.addAttribute("modalOpen", true);
                model.addAttribute("modalError", "Vui lòng nhập mật khẩu mới.");
                model.addAttribute("userForm", userForm);
                return "user/thong-tin";
            }
            if (newPassword.trim().length() < 6) {
                model.addAttribute("modalOpen", true);
                model.addAttribute("modalError", "Mật khẩu mới phải có tối thiểu 6 ký tự.");
                model.addAttribute("userForm", userForm);
                return "user/thong-tin";
            }
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("modalOpen", true);
                model.addAttribute("modalError", "Xác nhận mật khẩu mới không khớp.");
                model.addAttribute("userForm", userForm);
                return "user/thong-tin";
            }
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                model.addAttribute("modalOpen", true);
                model.addAttribute("modalError", "Mật khẩu hiện tại không chính xác.");
                model.addAttribute("userForm", userForm);
                return "user/thong-tin";
            }
            // All good - set encoded new password
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.save(user);

        // Nếu có booking tạm trong session thì hoàn tất lưu booking gắn user
        Object pending = session.getAttribute("PENDING_BOOKING");
        if (pending instanceof Map) {
            try {
                Map<?, ?> payload = (Map<?, ?>) pending;
                Object ngayDenObj = payload.get("ngayDen");
                Object ngayDiObj = payload.get("ngayDi");
                String ngayDen = ngayDenObj instanceof String ? (String) ngayDenObj : null;
                String ngayDi = ngayDiObj instanceof String ? (String) ngayDiObj : null;
                Booking b = new Booking();
                // generate simple code
                b.setCode("BK-" + System.currentTimeMillis());
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
                logger.warn("Error while completing pending booking: {}", ex.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công");
        return "redirect:/user/thong-tin";
    }

    @GetMapping("/form-register")
    public String formRegister() {
        return "auth/dang-ky";
    }

}
