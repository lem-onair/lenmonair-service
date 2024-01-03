package com.hanghae.lemonairservice.exception.channel;

public class NoOnAirChannelException extends RuntimeException {
	public static String errorMsg = "현재 진행중인 생방송이 없습니다.";

	public NoOnAirChannelException() {
		super(errorMsg);
	}

}
