package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class MemberNotFoundException extends ExpectedException {
	public MemberNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}

	public MemberNotFoundException() {
		super(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");
	}

}
