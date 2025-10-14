//package com.codegym.case_study_module4.config;
//
//
//import com.codegym.case_study_module4.service.IUserService;
//import com.codegym.case_study_module4.service.impl.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
////@Configuration
////@EnableWebSecurity
//public class SecurityConfig {
//    @Autowired
//    private IUserService userService;
//
//    // Mã hóa mật khẩu sử dụng BCrypt
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(10); // độ mạnh 10 vòng
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService); // sử dụng userService để lấy user từ DB
//        authProvider.setPasswordEncoder(passwordEncoder()); // sử dụng mã hoá BCrypt
//        return authProvider;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // Phân quyền cho từng loại request
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/", "/login", "/logout").permitAll()
//                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/user/**").hasRole("USER")
//                        .anyRequest().authenticated()
//                )
//                // Cho phép form login (dành cho người dùng web)
////                .formLogin(form -> form
////                        .loginPage("/login")
////                        .permitAll()
////                )
//                .formLogin(Customizer.withDefaults())
//                // Cho phép logout
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .permitAll()
//                )
//                // Cho phép basic auth cho REST client (Postman, mobile app)
//                .httpBasic(Customizer.withDefaults())
//                // CSRF: bật cho web, tắt cho API
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**") // tắt csrf riêng cho API
//                );
//        return http.build();
//    }
//}
