package com.merchant.config;

import com.merchant.entity.User;
import com.merchant.entity.UserRole;
import com.merchant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 只在没有用户时初始化数据
        if (userRepository.count() == 0) {
            // 创建超级管理员
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .email("admin@example.com")
                    .phone("13800000000")
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);

            // 创建商家用户
            User merchant = User.builder()
                    .username("merchant")
                    .password(passwordEncoder.encode("merchant123"))
                    .role(UserRole.MERCHANT)
                    .email("merchant@example.com")
                    .phone("13900000000")
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(merchant);

            System.out.println("初始化用户数据完成！");
            System.out.println("超级管理员 - 用户名: admin, 密码: admin123");
            System.out.println("商家用户 - 用户名: merchant, 密码: merchant123");
        }
    }
}
