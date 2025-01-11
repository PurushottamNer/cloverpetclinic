package com.clover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetServiceApplication.class, args);
	}

}
