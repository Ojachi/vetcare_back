package com.vetcare_back.controller.user;

import com.vetcare_back.dto.user.UserRegisterDTO;
import com.vetcare_back.dto.user.UserResponseDTO;
import com.vetcare_back.dto.user.UserUpdateDTO;
import com.vetcare_back.mapper.UserMapper;
import com.vetcare_back.service.IUserService;
import com.vetcare_back.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService userService;
    private final UserMapper userMapper;


    public UserController(UserServiceImpl userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(){
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getByEmail(currentUserEmail));
    }
}
