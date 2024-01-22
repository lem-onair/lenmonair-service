package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class ChannelSaveFailedException extends ExpectedException {
	public ChannelSaveFailedException(String message) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, message + " 개인방송 채널 저장 실패");
	}
}
