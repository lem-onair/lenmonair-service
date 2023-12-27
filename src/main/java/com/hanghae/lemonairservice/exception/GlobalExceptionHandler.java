package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice // 4-1. 비즈니스로직에서 발생한 예외에 대해서 client가 적절한 응답을 받아야 하는경우 사용합니다.
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NoOnAirChannelException.class) // 4-2. 어떤 예외 클래스가 발생했을 때 아래 메소드가 실행될 것인지, 예외 클래스를 지정합니다.
	// @ResponseStatus(HttpStatus.NOT_FOUND) // 4-5. 4-4에서 HttpStatus를 정의하여 반환하고 있으므로 현재는 필요가 없습니다.
	// 만약 아래 return 문을 제거하고 ResponseStatus 어노테이션 만을 사용한다면 에러메세지 없이 404 상태코드만 응답합니다.
	protected ResponseEntity<String> handleNoOnAirChannelException() {
		log.error("handle : " + NoOnAirChannelException.errorMsg); // 4-3. 서버단에서도 예외 발생 사실을 알 수 있도록 로깅합니다.
		return new ResponseEntity<>(NoOnAirChannelException.errorMsg,
			HttpStatus.NOT_FOUND); // 4-4. 최종적으로 client에게 전달될 ResponseEntity를 정의합니다.
	}
}
