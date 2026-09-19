package com.codevepa.vargox.repository;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByRole(Role role);

    boolean existsByUsername(String username);
}