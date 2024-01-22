package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class PasswordRetypeMismatchException extends ExpectedException {
	public PasswordRetypeMismatchException() {
		super(HttpStatus.UNPROCESSABLE_ENTITY, "비밀번호 확인 입력이 일치하지 않습니다.");
	}
}
