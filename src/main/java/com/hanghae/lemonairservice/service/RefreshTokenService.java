package com.hanghae.lemonairservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.refreshtoken.RefreshRequestDto;
import com.hanghae.lemonairservice.dto.refreshtoken.RefreshResponseDto;
import com.hanghae.lemonairservice.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
	private final JwtUtil jwtUtil;

	public Mono<RefreshResponseDto> refresh(RefreshRequestDto refreshRequestDto) {
		// TODO: 2023-12-19 redis 에 저장되어있는 해당 사용자의 refreshToken과 비교하여 refreshToken이 변조되지 않았다면 새로 토큰 발급
		log.info("refresh 서비스 접근");
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
