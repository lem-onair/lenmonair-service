package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class ChannelNotFoundException extends ExpectedException {
	public ChannelNotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
