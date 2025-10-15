package com.codegym.case_study_module4.controller.user;

import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.service.IBookingService;
import com.codegym.case_study_module4.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerPasswordTest {

    private IUserService userService;
    private IBookingService bookingService;
    private PasswordEncoder passwordEncoder;
    private UserController controller;

    @BeforeEach
    public void setup() {
        userService = mock(IUserService.class);
        bookingService = mock(IBookingService.class);
        passwordEncoder = new BCryptPasswordEncoder(10);
        controller = new UserController();
        // inject mocks via reflection since fields are not exposed
        TestUtils.injectField(controller, "userService", userService);
        TestUtils.injectField(controller, "bookingService", bookingService);
        TestUtils.injectField(controller, "passwordEncoder", passwordEncoder);
    }

    @Test
    public void testChangePasswordSuccess() {
        Users stored = new Users();
        stored.setId(1L);
        stored.setEmail("u@example.com");
        stored.setPassword(passwordEncoder.encode("oldpass"));
        stored.setUsername("User");

        when(userService.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(stored));

        Users form = new Users();
        form.setUsername("User");
        form.setCitizenIdNumber(stored.getCitizenIdNumber());
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);
        Principal p = () -> "u@example.com";
        RedirectAttributes ra = mock(RedirectAttributes.class);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        String res = controller.update(form, br, p, ra, session, model, "newpass123", "oldpass", "newpass123");
        // should redirect back to thong-tin
        assertEquals("redirect:/user/thong-tin", res);
        ArgumentCaptor<Users> capt = ArgumentCaptor.forClass(Users.class);
        verify(userService).save(capt.capture());
        Users saved = capt.getValue();
        assertTrue(passwordEncoder.matches("newpass123", saved.getPassword()));
    }

    @Test
    public void testChangePasswordWrongCurrent() {
        Users stored = new Users();
        stored.setId(2L);
        stored.setEmail("v@example.com");
        stored.setPassword(passwordEncoder.encode("oldpass"));

        when(userService.findByEmailIgnoreCase(anyString())).thenReturn(Optional.of(stored));

        Users form = new Users();
        BindingResult br = mock(BindingResult.class);
        when(br.hasErrors()).thenReturn(false);
        Principal p = () -> "v@example.com";
        RedirectAttributes ra = mock(RedirectAttributes.class);
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        String res = controller.update(form, br, p, ra, session, model, "newpass123", "wrongold", "newpass123");
        // Controller now returns the view 'user/thong-tin' and re-opens modal with error instead of redirect
        assertEquals("user/thong-tin", res);
        // save should not be called because password mismatch
        verify(userService, never()).save(any(Users.class));
        // controller should set modalOpen/modalError and return userForm in model
        verify(model).addAttribute(eq("modalOpen"), eq(true));
        verify(model).addAttribute(eq("modalError"), anyString());
        verify(model).addAttribute(eq("userForm"), any(Users.class));
    }
}
