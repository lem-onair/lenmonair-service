package com.hanghae.lemonairservice.exception.member;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class MemberIdAlreadyExistException extends ExpectedException {
	public MemberIdAlreadyExistException(String message) {
		super(HttpStatus.CONFLICT, message + "은(는) 이미 사용 중인 닉네임입니다.");
	}
}
