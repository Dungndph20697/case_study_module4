package com.codegym.case_study_module4.controller.user;

import com.codegym.case_study_module4.model.Booking;
import com.codegym.case_study_module4.model.Room;
import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.codegym.case_study_module4.service.IRoomService;
import com.codegym.case_study_module4.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller dành cho các API liên quan đến người dùng (được gọi từ client bằng AJAX).
 * Lý do tách thành @RestController: trả về JSON trực tiếp (Map) thay vì view/template.
 *
 * Endpoints:
 * - POST /user/update-ajax : cập nhật thông tin người dùng (username, phoneNumber, citizenIdNumber)
 *   và xử lý thay đổi mật khẩu nếu có (cần currentPassword). Nếu trong session có PENDING_BOOKING,
 *   phương thức sẽ hoàn tất việc lưu booking gắn với user vừa cập nhật.
 */
@RestController
@RequestMapping("/user")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    @Autowired
    private IUserService userService;


    @Autowired
    private IRoomService roomService;


    @Autowired
    private IBookingService bookingService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Xử lý yêu cầu AJAX để cập nhật thông tin người dùng.
     *
     * Quy trình (tóm tắt):
     * 1) Kiểm tra authentication (Principal) — nếu không có, trả về {ok: false, msg: "Vui lòng đăng nhập"}.
     * 2) Tìm người dùng theo email (principal.getName()). Nếu không tồn tại, trả về lỗi.
     * 3) Kiểm tra tính duy nhất của CCCD/CMND nếu client gửi giá trị mới; nếu trùng, trả về thông báo lỗi
     *    và chỉ rõ trường lỗi bằng key `field` trong response (ví dụ: "citizenIdNumber").
     * 4) Cập nhật các trường đơn giản (username, phoneNumber). Nếu client gửi currentPassword =>
     *    bắt đầu luồng đổi mật khẩu:
     *      - Kiểm tra newPassword không rỗng và >= 6 ký tự
     *      - Kiểm tra newPassword khớp confirmPassword
     *      - Kiểm tra currentPassword khớp mật khẩu hiện tại (dùng passwordEncoder.matches)
     *      - Nếu hợp lệ, mã hóa mật khẩu mới và gán cho user
     * 5) Lưu user (userService.save). Nếu session chứa PENDING_BOOKING (Map), hoàn tất việc tạo Booking
     *    gắn user (ví dụ: setCheckInDate/setCheckOutDate/ status ) và lưu qua bookingService, xóa PENDING_BOOKING
     *    khỏi session và trả về flag `completedBooking=true` trong response.
     * 6) Trả về Map JSON với các key hữu ích: ok (boolean), msg (message), username, phoneNumber, citizenIdNumber,
     *    và optionally completedBooking.
     *
     * Inputs (request parameters): username, phoneNumber, citizenIdNumber, password (newPassword),
     * currentPassword, confirmPassword. Ngoài ra Principal và HttpSession được inject.
     *
     * Outputs (JSON Map):
     * - Thành công: {ok: true, msg: 'Đã cập nhật', username, phoneNumber, citizenIdNumber, [completedBooking:true]}
     * - Lỗi xác thực: {ok:false, msg: 'Vui lòng đăng nhập'}
     * - Lỗi validate: {ok:false, msg: '...', field: '<tên trường bị lỗi>'}
     */
    @PostMapping("/update-ajax")
    public Map<String, Object> updateAjax(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "citizenIdNumber", required = false) String citizenIdNumber,
            @RequestParam(value = "password", required = false) String newPassword,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Principal principal,
            HttpSession session) {

        Map<String, Object> resp = new HashMap<>();
        resp.put("ok", false);

        // Log giúp debug khi cần
        try {
            logger.debug("updateAjax called by principal={}, params: username={}, phoneNumber={}, citizenIdNumber={}",
                    principal == null ? "<null>" : principal.getName(), username, phoneNumber, citizenIdNumber);
        } catch (Exception ex) {
            // ignore logging errors
        }

        // 1) Kiểm tra authentication
        if (principal == null) {
            resp.put("msg", "Vui lòng đăng nhập");
            return resp;
        }
        String email = principal.getName();

        // 2) Lấy đối tượng user hiện tại từ DB
        Optional<Users> opt = userService.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            resp.put("msg", "Người dùng không tồn tại");
            return resp;
        }
        Users user = opt.get();

        // 3) Kiểm tra CCCD/CMND (nếu client gửi và khác với giá trị cũ)
        if (citizenIdNumber != null && !citizenIdNumber.equals(user.getCitizenIdNumber())) {
            if (userService.existsByCitizenIdNumber(citizenIdNumber)) {
                resp.put("msg", "CCCD đã tồn tại");
                resp.put("field", "citizenIdNumber");
                return resp;
            }
            user.setCitizenIdNumber(citizenIdNumber);
        }

        // 4) Cập nhật các trường đơn giản
        if (username != null) user.setUsername(username);
        if (phoneNumber != null) user.setPhoneNumber(phoneNumber);

        // 5) Nếu có yêu cầu đổi mật khẩu (currentPassword được gửi), thực hiện validation và đổi mật khẩu
        if (currentPassword != null && !currentPassword.trim().isEmpty()) {
            // Kiểm tra newPassword tồn tại
            if (newPassword == null || newPassword.trim().isEmpty()) {
                resp.put("msg", "Vui lòng nhập mật khẩu mới.");
                resp.put("field", "password");
                return resp;
            }
            // Kiểm tra độ dài
            if (newPassword.trim().length() < 6) {
                resp.put("msg", "Mật khẩu mới phải có tối thiểu 6 ký tự.");
                resp.put("field", "password");
                return resp;
            }
            // Kiểm tra khớp confirm
            if (!newPassword.equals(confirmPassword)) {
                resp.put("msg", "Xác nhận mật khẩu mới không khớp.");
                resp.put("field", "confirmPassword");
                return resp;
            }
            // Kiểm tra currentPassword đúng
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                resp.put("msg", "Mật khẩu hiện tại không chính xác.");
                resp.put("field", "currentPassword");
                return resp;
            }
            // Gán mật khẩu mới (đã mã hóa)
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        // 6) Lưu và xử lý thêm (ví dụ hoàn tất booking tạm trong session)
        try {
            userService.save(user);
            // Nếu session chứa booking tạm (PENDING_BOOKING) thì hoàn tất lưu booking gắn user
            Object pending = session.getAttribute("PENDING_BOOKING");
            if (pending instanceof Map) {
                try {
                    Map<?, ?> payload = (Map<?, ?>) pending;
                    Object ngayDenObj = payload.get("ngayDen");
                    Object ngayDiObj = payload.get("ngayDi");
                    String ngayDen = ngayDenObj instanceof String ? (String) ngayDenObj : null;
                    String ngayDi = ngayDiObj instanceof String ? (String) ngayDiObj : null;
                    Booking b = new Booking();
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
                    resp.put("completedBooking", true);
                } catch (Exception ex) {
                    // ignore individual booking errors; không làm fail toàn bộ update user
                }
            }

            // 7) Trả về response thành công kèm thông tin cập nhật để client update UI
            resp.put("ok", true);
            resp.put("msg", "Đã cập nhật");
            resp.put("username", user.getUsername());
            resp.put("phoneNumber", user.getPhoneNumber());
            resp.put("citizenIdNumber", user.getCitizenIdNumber());
            return resp;
        } catch (Exception ex) {
            resp.put("msg", "Có lỗi khi lưu dữ liệu");
            return resp;
        }
    }


    @GetMapping("/get-rooms-available")
    public ResponseEntity<List<Room>> userDatPhong(@RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
                                                   @RequestParam("checkOut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        return ResponseEntity.ok(roomService.findAvailableRooms(checkIn, checkOut));
    }

    @GetMapping("/get-rooms")
    public ResponseEntity<List<Room>> userDatPhong() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @PostMapping("/dat-phong")
    public ResponseEntity<Booking> datPhong(@RequestBody Booking booking) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Users user = userService.findUsersByEmail(username);
        Long userId = user.getId();
        booking.setUser(Users.builder().id(userId).build());
        booking.setStatus(0);
        booking.setCode(bookingService.generateBookingCode());
        return ResponseEntity.ok(bookingService.save(booking));
    }

    @GetMapping("/get-bookings-user")
    public ResponseEntity<List<Booking>> getBookingsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); //

        Users user = userService.findUsersByEmail(username);
        Long userId = user.getId();
        List<Booking> bookings = bookingService.findByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/huy-dat-phong/{id}")
    public ResponseEntity<Booking> datPhong(@PathVariable Long id) {
        Optional<Booking> optionalBooking = bookingService.findById(id);
        if (!optionalBooking.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Booking booking = optionalBooking.get();
        booking.setStatus(2);
        return ResponseEntity.ok(bookingService.save(booking));
    }

}
