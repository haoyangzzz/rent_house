package com.house.rent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.house.rent.mapper")
@EnableTransactionManagement
public class RentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentApplication.class, args);
    }

}
