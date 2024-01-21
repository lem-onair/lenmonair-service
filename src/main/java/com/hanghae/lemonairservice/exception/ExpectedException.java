package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public class ExpectedException extends RuntimeException {
	private final HttpStatusCode code;

	public ExpectedException(HttpStatusCode code, String message) {
		super(message);
		this.code = code;
	}
}
