package com.codevepa.vargox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling 
@SpringBootApplication
public class VargoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(VargoxApplication.class, args);
	}

}
