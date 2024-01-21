package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(ExpectedException.class)
	protected Mono<ResponseEntity<String>> handleExpectedException(final ExpectedException exception) {
		return Mono.just(ResponseEntity.status(exception.getCode()).body(exception.getMessage()));
	}

	@ExceptionHandler(RuntimeException.class)
	protected Mono<ResponseEntity<String>> handleUnExpectedException() {
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예기치 못한 문제가 발생했습니다."));
	}
}
