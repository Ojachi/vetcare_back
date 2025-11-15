package com.vetcare_back.controller.user;

import com.vetcare_back.dto.user.ActivateUserDTO;
import com.vetcare_back.dto.user.ChangeRoleDTO;
import com.vetcare_back.dto.user.DeactivateUserDTO;
import com.vetcare_back.dto.user.UserResponseDTO;
import com.vetcare_back.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final IUserService userService;

    public AdminController(IUserService userService) {
        this.userService = userService;
    }

    @PutMapping("/users/role")
    public ResponseEntity<String> changeRole(@Valid @RequestBody ChangeRoleDTO dto){
        userService.changeRole(dto);
        return ResponseEntity.ok("Role changed");
    }

    @PutMapping("/users/activate")
    public ResponseEntity<String> activateUser(@Valid @RequestBody ActivateUserDTO dto) {
        userService.activate(dto);
        return ResponseEntity.ok("User activated successfully");
    }

    @PutMapping("/users/deactivate")
    public ResponseEntity<String> deactivateUser(@Valid @RequestBody DeactivateUserDTO dto){
        userService.deactivate(dto);
        return ResponseEntity.ok("User deactivated");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        return ResponseEntity.ok(userService.listAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.ok("User deleted");
    }
}
