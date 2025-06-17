package com.shopco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopcoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopcoApplication.class, args);
	}

}
