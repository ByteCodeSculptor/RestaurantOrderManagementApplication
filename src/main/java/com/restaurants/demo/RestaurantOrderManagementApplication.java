package com.restaurants.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RestaurantOrderManagementApplication {

	public static void main(String[] args) {
		//this to check the first Pull request
		SpringApplication.run(RestaurantOrderManagementApplication.class, args);
	}
}
