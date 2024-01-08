package com.hanghae.lemonairservice.service;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.token.RefreshRequestDto;
import com.hanghae.lemonairservice.jwt.JwtTokenSubjectDto;
import com.hanghae.lemonairservice.jwt.JwtUtil;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

	@InjectMocks
	RefreshTokenService refreshTokenService;

	@Mock
	JwtUtil jwtUtil;

	@Test
	void refreshSuccessTest(){
		RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder().refreshToken("1234").build();
		JwtTokenSubjectDto jwtTokenSubjectDto = JwtTokenSubjectDto.builder().loginId("kangminbeom").nickname("minbeom").build();
		given(jwtUtil.validateRefreshToken(refreshRequestDto.getRefreshToken())).willReturn(Mono.just(true));
		given(jwtUtil.getSubjectFromToken(refreshRequestDto.getRefreshToken())).willReturn(jwtTokenSubjectDto);
		given(jwtUtil.createAccessToken(jwtTokenSubjectDto.getLoginId(),jwtTokenSubjectDto.getNickname())).willReturn(Mono.just("12345"));

		StepVerifier.create(refreshTokenService.refresh(refreshRequestDto))
			.expectNextMatches(refresh ->{
				assertThat(refresh.getRefreshToken()).isEqualTo("12345");
				return true;
			}).verifyComplete();

		verify(jwtUtil).validateRefreshToken(refreshRequestDto.getRefreshToken());
		verify(jwtUtil).getSubjectFromToken(refreshRequestDto.getRefreshToken());
		verify(jwtUtil).createAccessToken(jwtTokenSubjectDto.getLoginId(),jwtTokenSubjectDto.getNickname());
	}

	@Test
	void refreshThrowsNotvaildTokenException(){
		RefreshRequestDto refreshRequestDto = RefreshRequestDto.builder().refreshToken("1234").build();
		JwtTokenSubjectDto jwtTokenSubjectDto = JwtTokenSubjectDto.builder().loginId("kangminbeom").nickname("minbeom").build();
		System.out.println("loginId: " + jwtTokenSubjectDto.getLoginId());
		given(jwtUtil.validateRefreshToken(refreshRequestDto.getRefreshToken())).willReturn(Mono.just(false));

		StepVerifier.create(refreshTokenService.refresh(refreshRequestDto))
			.expectErrorMatches(refresh ->
				refresh instanceof ResponseStatusException &&
				((ResponseStatusException)refresh).getStatusCode() == HttpStatus.UNAUTHORIZED &&
				refresh.getMessage().contains("유효하지 않은 토큰입니다.")
			).verify();

		verify(jwtUtil).validateRefreshToken(refreshRequestDto.getRefreshToken());
	}
}
