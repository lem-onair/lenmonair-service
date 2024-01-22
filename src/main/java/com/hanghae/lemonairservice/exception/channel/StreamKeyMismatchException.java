package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class StreamKeyMismatchException extends ExpectedException {
	public StreamKeyMismatchException() {
		super(HttpStatus.CONFLICT, "스트림 키가 일치하지 않습니다.");
	}
}
