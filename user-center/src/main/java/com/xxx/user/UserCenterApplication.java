package com.xxx.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 描述：UserCenter
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/12 15:28
 * @company gsName
 */
@SpringBootApplication(scanBasePackages = "com.xxx")
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }
}
