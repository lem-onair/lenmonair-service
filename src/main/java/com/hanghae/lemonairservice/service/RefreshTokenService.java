package com.hanghae.lemonairservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.token.RefreshRequestDto;
import com.hanghae.lemonairservice.dto.token.RefreshResponseDto;
import com.hanghae.lemonairservice.jwt.JwtTokenSubjectDto;
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
		return jwtUtil.validateRefreshToken(refreshRequestDto.getRefreshToken()).flatMap(isValidate -> {
			if (isValidate) {
				JwtTokenSubjectDto jwtTokenSubjectDto = jwtUtil.getSubjectFromToken(
					refreshRequestDto.getRefreshToken());
				return jwtUtil.createAccessToken(jwtTokenSubjectDto.getLoginId(), jwtTokenSubjectDto.getNickname())
					.map(RefreshResponseDto::new);
			} else {
				return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."));
			}
		});
	}

}
