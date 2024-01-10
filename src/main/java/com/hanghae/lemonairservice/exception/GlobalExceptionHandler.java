package com.hanghae.lemonairservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.hanghae.lemonairservice.exception.channel.NoExistChannelException;
import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;
import com.hanghae.lemonairservice.exception.channel.OffAirBroadCastException;
import com.hanghae.lemonairservice.exception.member.AlreadyExistEmailException;
import com.hanghae.lemonairservice.exception.member.AlreadyExistIdException;
import com.hanghae.lemonairservice.exception.member.AlreadyExistNicknameException;
import com.hanghae.lemonairservice.exception.member.NotEqualPasswordException;
import com.hanghae.lemonairservice.exception.member.PasswordFormException;
import com.hanghae.lemonairservice.exception.point.FailedAddPointException;
import com.hanghae.lemonairservice.exception.point.NoDonationLogException;
import com.hanghae.lemonairservice.exception.point.NotEnoughPointException;
import com.hanghae.lemonairservice.exception.point.NotExistUserException;
import com.hanghae.lemonairservice.exception.point.NotUsepointToSelfException;
import com.hanghae.lemonairservice.exception.refreshtoken.NotvalidTokenException;
import com.hanghae.lemonairservice.exception.stream.NoStartedAtLogException;
import com.hanghae.lemonairservice.exception.stream.NotEqualsStreamKeysException;
import com.hanghae.lemonairservice.exception.stream.NotExistsChannelException;
import com.hanghae.lemonairservice.exception.stream.NotExistsIdException;

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

	@ExceptionHandler(NoExistChannelException.class)
	protected ResponseEntity<String> handleNoExistChannelException() {
		log.error("handle : " + NoExistChannelException.errorMsg);
		return new ResponseEntity<>(NoExistChannelException.errorMsg,
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OffAirBroadCastException.class)
	protected ResponseEntity<String> handleoffAirBroadCastException() {
		log.error("handle : " + OffAirBroadCastException.errorMsg);
		return new ResponseEntity<>(OffAirBroadCastException.errorMsg,
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(PasswordFormException.class)
	protected ResponseEntity<String> handlePasswordFormException() {
		log.error("handle : " + PasswordFormException.errorMsg);
		return new ResponseEntity<>(PasswordFormException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotEqualPasswordException.class)
	protected ResponseEntity<String> handleNotEqualPasswordException() {
		log.error("handle : " + NotEqualPasswordException.errorMsg);
		return new ResponseEntity<>(NotEqualPasswordException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistEmailException.class)
	protected ResponseEntity<String> handleAlreadyExistEmailException() {
		log.error("handle : " + AlreadyExistEmailException.errorMsg);
		return new ResponseEntity<>(AlreadyExistEmailException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistIdException.class)
	protected ResponseEntity<String> handleAlreadyExistIdException() {
		log.error("handle : " + AlreadyExistIdException.errorMsg);
		return new ResponseEntity<>(AlreadyExistIdException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistNicknameException.class)
	protected ResponseEntity<String> handleAlreadyExistNicknameException() {
		log.error("handle : " + AlreadyExistNicknameException.errorMsg);
		return new ResponseEntity<>(AlreadyExistNicknameException.errorMsg,
			HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(NotExistUserException.class)
	protected ResponseEntity<String> handleNotExistUserException() {
		log.error("handle : " + NotExistUserException.errorMsg);
		return new ResponseEntity<>(NotExistUserException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FailedAddPointException.class)
	protected ResponseEntity<String> handleFailedAddPointException() {
		log.error("handle : " + FailedAddPointException.errorMsg);
		return new ResponseEntity<>(FailedAddPointException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotUsepointToSelfException.class)
	protected ResponseEntity<String> handleNotUsepointToYouException() {
		log.error("handle : " + NotUsepointToSelfException.errorMsg);
		return new ResponseEntity<>(NotUsepointToSelfException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotEnoughPointException.class)
	protected ResponseEntity<String> handleNoPointException() {
		log.error("handle : " + NotEnoughPointException.errorMsg);
		return new ResponseEntity<>(NotEnoughPointException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoDonationLogException.class)
	protected ResponseEntity<String> handleNoDonationLogException() {
		log.error("handle : " + NoDonationLogException.errorMsg);
		return new ResponseEntity<>(NoDonationLogException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotvalidTokenException.class)
	protected ResponseEntity<String> handleNotvalidTokenException() {
		log.error("handle : " + NotvalidTokenException.errorMsg);
		return new ResponseEntity<>(NotvalidTokenException.errorMsg,
			HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(NotEqualsStreamKeysException.class)
	protected ResponseEntity<String> handleNotEqualsStreamKeysException() {
		log.error("handle : " + NotEqualsStreamKeysException.errorMsg);
		return new ResponseEntity<>(NotEqualsStreamKeysException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotExistsIdException.class)
	protected ResponseEntity<String> handleNotExistsIdException(String streamerId) {
		log.error("handle : " + NotExistsIdException.errorMsg(streamerId));
		return new ResponseEntity<>(NotExistsIdException.errorMsg(streamerId),
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NotExistsChannelException.class)
	protected ResponseEntity<String> handleNotExistsChannelException() {
		log.error("handle : " + NotExistsChannelException.errorMsg);
		return new ResponseEntity<>(NotExistsChannelException.errorMsg,
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NoStartedAtLogException.class)
	protected ResponseEntity<String> handleNotOnAirChannelException() {
		log.error("handle : " + NoStartedAtLogException.errorMsg);
		return new ResponseEntity<>(NoStartedAtLogException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}
}
