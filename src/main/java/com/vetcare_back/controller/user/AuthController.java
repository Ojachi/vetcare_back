package com.vetcare_back.controller.user;

import com.vetcare_back.dto.user.UserResponseDTO;
import com.vetcare_back.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private IUserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDTO userResponse = userService.getByEmail(email);
        return ResponseEntity.ok(userResponse);
    }
}