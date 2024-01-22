package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class PasswordMismatchException extends ExpectedException {

	public PasswordMismatchException() {
		super(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
	}
}
