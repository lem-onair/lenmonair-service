package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class LoginIdAlreadyExistException extends ExpectedException {

	public LoginIdAlreadyExistException(String message) {

		super(HttpStatus.CONFLICT, message + " 아이디는 이미 사용중입니다.");
	}
}
