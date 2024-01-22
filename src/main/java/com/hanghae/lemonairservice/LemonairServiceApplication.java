package com.hanghae.lemonairservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class LemonairServiceApplication {

	public static void main(String[] args) {
		BlockHound.install();
		SpringApplication.run(LemonairServiceApplication.class, args);
	}

}
