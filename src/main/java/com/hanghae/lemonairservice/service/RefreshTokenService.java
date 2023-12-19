package com.hanghae.lemonairservice.service;

import java.io.NotActiveException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.refreshtoken.RefreshRequestDto;
import com.hanghae.lemonairservice.dto.refreshtoken.RefreshResponseDto;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import reactor.core.publisher.Mono;

@Service
public class RefreshTokenService {
	private RefreshTokenRepository refreshTokenRepository;
	private JwtUtil jwtUtil;
	public Mono<RefreshResponseDto> refresh(RefreshRequestDto refreshRequestDto) {
		return jwtUtil.validateRefreshToken(refreshRequestDto.getRefreshToken())
			.flatMap(isValidate -> {
				if (isValidate) {
					String uid = jwtUtil.getUserInfoFromToken(refreshRequestDto.getRefreshToken());
					return jwtUtil.createToken(uid)
						.map(RefreshResponseDto::new);
				} else {
					return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."));
				}
			});
	}

}
