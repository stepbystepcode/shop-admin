package com.merchant.config;

import com.merchant.entity.*;
import com.merchant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private OperationLogRepository operationLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        initializeUsers();
        initializeMerchants();
        initializeOperationLogs();
    }

    private void initializeUsers() {
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

    private void initializeMerchants() {
        // 只在没有商家时初始化数据
        if (merchantRepository.count() == 0) {
            String[] businessTypes = {"餐饮", "零售", "服装", "电子", "美妆", "家居"};
            MerchantStatus[] statuses = {MerchantStatus.PENDING, MerchantStatus.APPROVED, MerchantStatus.REJECTED};

            // 创建30个商家
            for (int i = 1; i <= 30; i++) {
                String businessType = businessTypes[random.nextInt(businessTypes.length)];
                MerchantStatus status = statuses[random.nextInt(statuses.length)];
                LocalDateTime createTime = LocalDateTime.now().minusDays(random.nextInt(7));

                Merchant merchant = Merchant.builder()
                        .name("商家" + i)
                        .businessType(businessType)
                        .status(status)
                        .contactName("联系人" + i)
                        .contactPhone("138" + String.format("%08d", i))
                        .email("merchant" + i + "@example.com")
                        .address("北京市朝阳区XX街" + i + "号")
                        .description("这是一个" + businessType + "类型的商家")
                        .businessLicense("BL" + String.format("%08d", i))
                        .createdAt(createTime)
                        .updatedAt(createTime)
                        .build();
                merchantRepository.save(merchant);
            }
            System.out.println("初始化商家数据完成！");
        }
    }

    private void initializeOperationLogs() {
        // 只在没有日志时初始化数据
        if (operationLogRepository.count() == 0) {
            String[] operations = {
                "查询商家列表", "查看商家详情", "审核商家申请", "更新商家状态",
                "导出商家数据", "查看统计报表", "系统登录", "系统登出"
            };

            // 为admin用户创建50条操作日志
            for (int i = 1; i <= 50; i++) {
                String operation = operations[random.nextInt(operations.length)];
                LocalDateTime createTime = LocalDateTime.now().minusDays(random.nextInt(7))
                        .minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60));

                OperationLog log = OperationLog.builder()
                        .username("admin")
                        .operation(operation)
                        .method("GET")
                        .params("id=" + random.nextInt(30))
                        .ip("192.168.1." + random.nextInt(255))
                        .createTime(createTime)
                        .build();
                operationLogRepository.save(log);
            }
            System.out.println("初始化操作日志完成！");
        }
    }
}
