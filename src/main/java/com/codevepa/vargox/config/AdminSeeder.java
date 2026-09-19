package com.codevepa.vargox.config;

import com.codevepa.vargox.entities.User;
import com.codevepa.vargox.enums.Role;
import com.codevepa.vargox.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminSeeder {

    private static final Logger logger = LoggerFactory.getLogger(AdminSeeder.class);

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner seedAdmin(UserRepo userRepo) {
        return args -> {
            if (userRepo.findByRole(Role.ADMIN).isEmpty()) {
                //FLAGGING THIS FOR BCRYPT
                User admin = new User(adminUsername, adminPassword, Role.ADMIN);
                userRepo.save(admin);
                logger.info("Seeded initial admin user: {}", adminUsername);
            } else {
                logger.info("Admin user already exists, skipping seed.");
            }
        };
    }
}