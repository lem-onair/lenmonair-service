package com.hanghae.lemonairservice.util;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;

public class ResponseMapper {
	public static <T> Mono<ResponseEntity<T>> mapToResponse(T value) {
		return Mono.just(ResponseEntity.ok(value));
	}

}
