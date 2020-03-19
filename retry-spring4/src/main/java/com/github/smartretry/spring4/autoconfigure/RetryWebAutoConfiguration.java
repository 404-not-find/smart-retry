package com.github.smartretry.spring4.autoconfigure;

import com.github.smartretry.spring4.admin.AdminController;
import org.springframework.context.annotation.Bean;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RetryWebAutoConfiguration {

    @Bean
    public AdminController retryAdminController() {
        return new AdminController();
    }
}