package com.vetcare_back.service;

import com.vetcare_back.dto.user.*;

import java.util.List;


public interface IUserService {
    UserResponseDTO register(UserRegisterDTO dto);
    UserResponseDTO login(UserLoginDTO dto);
    UserResponseDTO update(Long id, UserUpdateDTO dto);
    void delete(Long id);
    void changeRole(ChangeRoleDTO dto);
    void deactivate(DeactivateUserDTO dto);
    UserResponseDTO getById(Long id);
    UserResponseDTO getByEmail(String email);
    List<UserResponseDTO> listAll();
}
