package com.avalon.packer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.avalon"})
@MapperScan(basePackages = {"com.avalon.packer.mapper"})
public class PackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PackerApplication.class,args);
    }
}
