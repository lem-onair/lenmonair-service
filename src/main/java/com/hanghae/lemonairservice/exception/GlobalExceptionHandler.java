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
import com.hanghae.lemonairservice.exception.stream.StreamStartedAtIsNullException;
import com.hanghae.lemonairservice.exception.stream.StreamKeysNotEqualException;
import com.hanghae.lemonairservice.exception.stream.ChannelNotExistsException;
import com.hanghae.lemonairservice.exception.stream.MemberNotFoundByLoginIdException;

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

	@ExceptionHandler(StreamKeysNotEqualException.class)
	protected ResponseEntity<String> handleNotEqualsStreamKeysException() {
		log.error("handle : " + StreamKeysNotEqualException.errorMsg);
		return new ResponseEntity<>(StreamKeysNotEqualException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MemberNotFoundByLoginIdException.class)
	protected ResponseEntity<String> handleNotExistsIdException(String streamerId) {
		log.error("handle : " + MemberNotFoundByLoginIdException.errorMsg(streamerId));
		return new ResponseEntity<>(MemberNotFoundByLoginIdException.errorMsg(streamerId),
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ChannelNotExistsException.class)
	protected ResponseEntity<String> handleNotExistsChannelException() {
		log.error("handle : " + ChannelNotExistsException.errorMsg);
		return new ResponseEntity<>(ChannelNotExistsException.errorMsg,
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(StreamStartedAtIsNullException.class)
	protected ResponseEntity<String> handleNotOnAirChannelException() {
		log.error("handle : " + StreamStartedAtIsNullException.errorMsg);
		return new ResponseEntity<>(StreamStartedAtIsNullException.errorMsg,
			HttpStatus.BAD_REQUEST);
	}
}
