package com.codegym.case_study_module4.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileDto {

    @NotBlank(message = "Họ và tên không được để trống")
    private String username;

    // email hiển thị readonly nhưng giữ để bind vào form
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{9,11}", message = "Số điện thoại không hợp lệ (9-11 chữ số)")
    private String phoneNumber;

    @NotBlank(message = "CCCD không được để trống")
    @Pattern(regexp = "\\d{9,12}", message = "CCCD không hợp lệ (9-12 chữ số)")
    private String citizenIdNumber;
}

