package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class NicknameAlreadyExistException extends ExpectedException {

	public NicknameAlreadyExistException(String message) {
		super(HttpStatus.CONFLICT, message + " 닉네임은 이미 사용중입니다.");
	}
}
