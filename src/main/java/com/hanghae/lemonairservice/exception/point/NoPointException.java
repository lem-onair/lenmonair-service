package com.hanghae.lemonairservice.exception.point;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoPointException extends ResponseStatusException {
	// 3-1. 개발자가 예외 발생시 확인할 수 있는 에러 메세지를 정의합니다.
	// 실제로는 개발자만을 위한 에러메세지이지만, 저희 프로젝트에서는 단순화를 위해서 최종 client에게도 아래의 메세지가 보입니다.
	// 4. 이 Custom Exception Class가 어떻게 client에게 보일 수 있는지 확인하려면 exception 패키지의 GlobalExceptionHandler를 참고하세요
	public static String errorMsg = "후원 할 수 있는 금액이 부족합니다.";

	// 3-2. throw new NoOnAirChannelException() 구문으로 예외를 throw할 수 있도록 기본 생성자를 정의합니다.
	public NoPointException() {
		super(HttpStatus.BAD_REQUEST,errorMsg);
	}

}
