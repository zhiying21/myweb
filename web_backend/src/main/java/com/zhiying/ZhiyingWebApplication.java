package com.zhiying;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 个人网站后端启动类
 */
@SpringBootApplication
@MapperScan("com.zhiying.mapper")
public class ZhiyingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiyingWebApplication.class, args);
    }
}
