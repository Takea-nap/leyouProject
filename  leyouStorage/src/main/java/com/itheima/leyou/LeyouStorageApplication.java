package com.itheima.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@EnableEurekaClient
@SpringBootApplication
public class LeyouStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouStorageApplication.class, args);
    }

}
