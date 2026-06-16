package org.example.zhikaoyunexamplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.example.zhikaoyunexamplatform.**.mapper")
@EnableScheduling
public class ZhikaoyunExamPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhikaoyunExamPlatformApplication.class, args);
    }

}
