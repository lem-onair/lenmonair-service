package com.hanghae.lemonairservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.token.RefreshRequestDto;
import com.hanghae.lemonairservice.dto.token.RefreshResponseDto;
import com.hanghae.lemonairservice.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RefreshTokenController {
	private final RefreshTokenService refreshService;

	@PostMapping("/auth/refresh")
	public Mono<RefreshResponseDto> refresh(@RequestBody RefreshRequestDto refreshRequestDto) {
		return refreshService.refresh(refreshRequestDto);
	}

}
