package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class ChannelSaveFailedException extends ExpectedException {
	public ChannelSaveFailedException(String message) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, message + "회원의 개인방송 채널 저장 실패");
	}

	public ChannelSaveFailedException(HttpStatusCode code, String message) {
		super(code, message);
	}
}
