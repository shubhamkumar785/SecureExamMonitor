package com.exam.backend.config;

import com.exam.backend.auth.AppUser;
import com.exam.backend.auth.AppUserRepository;
import com.exam.backend.auth.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedUsersConfig {

    @Bean
    CommandLineRunner seedDefaultUsers(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN);
                appUserRepository.save(admin);
            }

            if (appUserRepository.findByUsername("student1").isEmpty()) {
                AppUser student = new AppUser();
                student.setUsername("student1");
                student.setPassword(passwordEncoder.encode("student123"));
                student.setRole(Role.ROLE_STUDENT);
                appUserRepository.save(student);
            }
        };
    }
}
