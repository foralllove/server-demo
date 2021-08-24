package com.xxx.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 描述：AuthCenterApplication
 *
 * @author 归墟
 * @email huanghe@shzx.com
 * @date 2021/8/14 11:31
 * @company 数海掌讯
 */
@EnableDiscoveryClient
@SpringBootApplication
public class AuthCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApplication.class, args);
    }
}
