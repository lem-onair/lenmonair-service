package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class ChannelNotFoundException extends ExpectedException {
	public ChannelNotFoundException() {
		super(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
	}
}
