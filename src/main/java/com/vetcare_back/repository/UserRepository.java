package com.vetcare_back.repository;

import com.vetcare_back.entity.Role;
import com.vetcare_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRoleAndActiveTrue(Role role);
    List<User> findByRoleInAndActiveTrue(List<Role> roles);
}
