package com.vetcare_back.service;

import com.vetcare_back.dto.user.*;
import com.vetcare_back.exception.*;
import com.vetcare_back.entity.User;
import com.vetcare_back.mapper.UserMapper;
import com.vetcare_back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @Override
    public UserResponseDTO register(UserRegisterDTO dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new EmailAlreadyExistsExeption("Email already exists");
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO login(UserLoginDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundExeption("Invalid email or password"));
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new SecurityException("Invalid email or password");
        }
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!user.getEmail().equals(currentUserEmail) && !hasRole("ADMIN")){
            throw new SecurityException("Unauthorized access");
        }
        userMapper.updateEntity(dto,user);
        user = userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void changeRole(ChangeRoleDTO dto) {
        if(!hasRole("ADMIN")) throw new SecurityException("Only admins can change roles");
        User user = userRepository.findById(dto.getId())
                .orElseThrow(()-> new UserNotFoundExeption("User not found"));
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(user.getEmail().equals(currentUserEmail)){
            throw new SecurityException("Admins cannot change their own role");
        }
        userMapper.updateRole(dto, user);
        userRepository.save(user);
    }

    @Override
    public void activate(ActivateUserDTO dto) {
        // Solo ADMIN puede activar usuarios
        if (!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can activate users");
        }

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));

        // Verificar que el usuario esté desactivado
        if (user.getActive()) {
            throw new IllegalStateException("User is already active");
        }

        // No permitir que un admin se active a sí mismo si fue desactivado
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getEmail().equals(currentUserEmail)) {
            throw new SecurityException("Admins cannot activate themselves");
        }

        // Activar el usuario usando el mapper existente
        userMapper.activateUser(dto, user);
        userRepository.save(user);
    }
    @Override
    public void deactivate(DeactivateUserDTO dto) {
        if(!hasRole("ADMIN")) {
            throw new SecurityException("Only admins can deactivate users");
        }

        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));

        // Evitar que un admin se desactive a sí mismo
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(user.getEmail().equals(currentUserEmail)){
            throw new SecurityException("Admins cannot deactivate themselves");
        }

        // Verificar que el usuario no esté ya desactivado
        if (!user.getActive()) {
            throw new IllegalStateException("User is already deactivated");
        }

        userMapper.deactivateUser(dto, user);
        userRepository.save(user);
    }
    
    @Override
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundExeption("User not found"));
        return userMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> listAll() {
        if(!hasRole("ADMIN")) throw new SecurityException("Only admins can list all users");
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean hasRole(String role) {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}

