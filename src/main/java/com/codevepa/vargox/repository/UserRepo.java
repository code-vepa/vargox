package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByRole(Role role);

    boolean existsByUsername(String username);
}