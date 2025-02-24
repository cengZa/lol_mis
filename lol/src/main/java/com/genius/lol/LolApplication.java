package com.genius.lol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.genius.lol.mapper")
public class LolApplication {

    public static void main(String[] args) {
        SpringApplication.run(LolApplication.class, args);
    }

}
