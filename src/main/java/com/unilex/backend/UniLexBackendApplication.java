package com.unilex.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * UniLex后端服务启动类
 */
@SpringBootApplication
@MapperScan("com.unilex.backend.mapper")
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class UniLexBackendApplication {

    /**
     * 应用程序入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UniLexBackendApplication.class, args);
    }

}
