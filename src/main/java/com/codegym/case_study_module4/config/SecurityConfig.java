package com.codegym.case_study_module4.config;


import com.codegym.case_study_module4.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private IUserService userService;

    // Mã hóa mật khẩu sử dụng BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10); // độ mạnh 10 vòng
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService((UserDetailsService) userService); // sử dụng userService để lấy user từ DB
        authProvider.setPasswordEncoder(passwordEncoder()); // sử dụng mã hoá BCrypt
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Phân quyền cho từng loại request
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login","user/form-register","user/register","user/bang-dieu-khien").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                // Cho phép logout (POST request with CSRF token)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // Cho phép basic auth cho REST client (Postman, mobile app)
                .httpBasic(Customizer.withDefaults())
                // CSRF: bật cho web, tắt cho API. Không tắt CSRF cho /user/** vì form web cần token
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/**", "/user/**", "/api/**")
                );
        return http.build();
    }
}