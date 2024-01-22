package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class BroadcastAlreadyStartedException extends ExpectedException {
	public BroadcastAlreadyStartedException(String loginId) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, loginId + "의 방송은 이미 시작되었습니다.");
	}
}
