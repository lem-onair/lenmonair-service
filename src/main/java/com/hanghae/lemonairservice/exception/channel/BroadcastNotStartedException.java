package com.hanghae.lemonairservice.exception.channel;

import org.springframework.http.HttpStatus;

import com.hanghae.lemonairservice.exception.ExpectedException;

public class BroadcastNotStartedException extends ExpectedException {
	public BroadcastNotStartedException(String message) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}
}
