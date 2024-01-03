package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NoOnAirChannelException.class)
	protected ResponseEntity<String> handleNoOnAirChannelException() {
		log.error("handle : " + NoOnAirChannelException.errorMsg);
		return new ResponseEntity<>(NoOnAirChannelException.errorMsg,
			HttpStatus.NOT_FOUND);
	}
}
