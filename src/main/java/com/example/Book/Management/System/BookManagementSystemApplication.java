package com.example.Book.Management.System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookManagementSystemApplication {

	public static void main(String[] args){
		SpringApplication.run(BookManagementSystemApplication.class, args);
	}

}
