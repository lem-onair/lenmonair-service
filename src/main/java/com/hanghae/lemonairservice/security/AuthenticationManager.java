package com.hanghae.lemonairservice.security;

import java.util.Collections;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.token.RefreshRequestDto;
import com.hanghae.lemonairservice.dto.token.RefreshResponseDto;
import com.hanghae.lemonairservice.jwt.JwtTokenSubjectDto;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;
import com.hanghae.lemonairservice.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;
	private final RefreshTokenService refreshTokenService;

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		JwtTokenSubjectDto jwtTokenSubjectDto =jwtUtil.getSubjectFromToken(authToken);
		String loginId = jwtTokenSubjectDto.getLoginId();
		log.info(loginId + " 의 사용자에 대해 인증 토큰 검증 시작");
		return jwtUtil.validateToken(authToken).flatMap(valid -> {
			if (valid) {
				return userDetailsService.findByUsername(loginId).map(user -> {
					Set<GrantedAuthority> emptyAuthorities = Collections.emptySet();
					return new UsernamePasswordAuthenticationToken(user, null, emptyAuthorities);
				});
			} else {
				return refreshTokenRepository.findByLoginId(loginId)
					.flatMap(
						refreshToken -> generateNewAccessToken(refreshToken) // Refresh Token을 이용하여 새로운 Access Token 생성
							.flatMap(newAccessToken -> userDetailsService.findByUsername(loginId).map(user -> {
								Set<GrantedAuthority> emptyAuthorities = Collections.emptySet();
								return new UsernamePasswordAuthenticationToken(user, null, emptyAuthorities);
							})))
					.switchIfEmpty(Mono.error(new AuthenticationCredentialsNotFoundException("리프레시 토큰이 없습니다.")));
			}
		});
	}

	private Mono<String> generateNewAccessToken(String refreshToken) {
		// 여기에 Refresh Token을 이용하여 새로운 Access Token을 생성하는 로직을 구현해야 합니다.
		// 예시: Refresh Token으로 새로운 Access Token을 발급받는 API 호출이나 직접 로직 구현
		// 실제로는 해당 로직을 구현해야 합니다.
		// 예시: return WebClient.create().post().uri("/refresh-token").bodyValue(refreshToken).retrieve().bodyToMono(String.class);
		// String refreshTokenEndpoint = "http://localhost:8081/api/auth/refresh"; // 새로운 Access Token을 발급받는 API 엔드포인트
		//
		// WebClient.RequestBodySpec request = (WebClient.RequestBodySpec)WebClient.create()
		// 	.post()
		// 	.uri(refreshTokenEndpoint)
		// 	.bodyValue(new RefreshRequestDto(refreshToken)); // Refresh Token을 요청에 첨부

		return refreshTokenService.refresh(new RefreshRequestDto(refreshToken))
			.map(RefreshResponseDto::getRefreshToken);

		// return request.retrieve().bodyToMono(String.class); // 발급받은 새로운 Access Token을 Mono로 반환
	}
}
