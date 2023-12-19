package com.hanghae.lemonairservice.repository;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Repository
public class RefreshTokenRepository {

	private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

	public RefreshTokenRepository(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	public Mono<String> findByLoginId(String loginId) {
		return reactiveRedisTemplate.opsForValue().get(loginId);
	}

	public Mono<Long> deleteByLoginId(String loginId) {
		return reactiveRedisTemplate.delete(loginId);
	}

	public Mono<String> saveRefreshToken(String loginId, String refreshToken) {
		return reactiveRedisTemplate.opsForValue().set(loginId, refreshToken)
			.flatMap(success -> {
				if (success) {
					return Mono.just(refreshToken);
				} else {
					return Mono.error(new RuntimeException("Refresh Token에 저장 중 에러가 발생했습니다."));
				}
			});
	}

	public Mono<Boolean> existsByLoginId(String loginId) {
		return reactiveRedisTemplate.hasKey(loginId);
	}
}
