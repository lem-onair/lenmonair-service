package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class EmailAlreadyExistException extends ExpectedException {

	public EmailAlreadyExistException(String message) {

		super(HttpStatus.CONFLICT, message + " 이메일은 이미 사용중입니다.");
	}
}
