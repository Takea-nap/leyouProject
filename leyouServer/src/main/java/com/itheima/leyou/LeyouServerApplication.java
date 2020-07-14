package com.itheima.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class LeyouServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouServerApplication.class, args);
    }

}
