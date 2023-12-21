package com.hanghae.lemonairservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping("/health")
	public ResponseEntity<Boolean> healthCheck(){
		return ResponseEntity.ok(true);
	}
}
