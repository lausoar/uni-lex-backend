package com.unilex.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.unilex.backend.mapper")
public class UniLexBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniLexBackendApplication.class, args);
    }

}
