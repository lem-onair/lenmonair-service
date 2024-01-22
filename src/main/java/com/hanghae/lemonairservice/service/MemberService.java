package com.hanghae.lemonairservice.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.exception.member.EmailAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.MemberIdAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.MemberNotFoundException;
import com.hanghae.lemonairservice.exception.member.NicknameAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.PasswordMismatchException;
import com.hanghae.lemonairservice.exception.member.PasswordRetypeMismatchException;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	private final MemberRepository memberRepository;
	private final MemberChannelService memberChannelService;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public Mono<SignUpResponseDto> signup(SignUpRequestDto signupRequestDto) {
		return passwordRetypeCheck(signupRequestDto).filter(checkResult -> checkResult)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new PasswordRetypeMismatchException())))
			.then(Mono.zip(memberRepository.existsByEmail(signupRequestDto.getEmail()),
				memberRepository.existsByNickname(signupRequestDto.getNickname()),
				memberRepository.existsByLoginId(signupRequestDto.getLoginId())).flatMap(tuple -> {
				if (tuple.getT1()) {
					return Mono.defer(() -> Mono.error(new EmailAlreadyExistException(signupRequestDto.getEmail())));
				} else if (tuple.getT2()) {
					return Mono.defer(
						() -> Mono.error(new NicknameAlreadyExistException(signupRequestDto.getNickname())));
				} else if (tuple.getT3()) {
					return Mono.defer(
						() -> Mono.error(new MemberIdAlreadyExistException(signupRequestDto.getLoginId())));
				} else {
					return saveMember(signupRequestDto).flatMap(
						saveMember -> memberChannelService.createMemberChannel(saveMember)
							.then(Mono.just(new SignUpResponseDto(saveMember.getStreamKey()))));
				}
			}));
	}

	public Mono<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
		return memberRepository.findByLoginId(loginRequestDto.getLoginId())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new MemberNotFoundException("가입되지 않은 회원입니다."))))
			.filter(findMember -> passwordCheck(loginRequestDto.getPassword(), findMember.getPassword()))
			.switchIfEmpty(Mono.defer(() -> Mono.error(PasswordMismatchException::new)))
			.flatMap(validFindMember -> Mono.zip(jwtUtil.createAccessToken(validFindMember),
					jwtUtil.createRefreshToken(validFindMember)
						.flatMap(refreshToken -> refreshTokenRepository.saveRefreshToken(validFindMember.getLoginId(),
							refreshToken)))
				.flatMap(tuple -> Mono.just(new LoginResponseDto(tuple.getT1(), tuple.getT2()))));
	}

	public Mono<String> logout(String loginId) {
		return refreshTokenRepository.deleteByLoginId(loginId).flatMap(logout -> Mono.just("로그아웃되었습니다."));
	}

	private Mono<Boolean> passwordRetypeCheck(SignUpRequestDto signUpRequestDto) {
		return Mono.fromCallable(() -> signUpRequestDto.getPassword().equals(signUpRequestDto.getPassword2()));
	}

	private boolean passwordCheck(String input, String encryptedPassword) {
		return passwordEncoder.matches(input, encryptedPassword);
	}

	@NotNull
	private Mono<Member> saveMember(SignUpRequestDto signupRequestDto) {
		return memberRepository.save(
				new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword())))
			.publishOn(Schedulers.boundedElastic())
			.onErrorResume(throwable -> Mono.error(
				new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다.")));
	}

	public Mono<Member> findById(Long memberId) {
		return memberRepository.findById(memberId)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new MemberNotFoundException("회원 정보를 찾을 수 없습니다."))));
	}

	public Mono<Member> findByLoginId(String memberLoginId) {
		return memberRepository.findByLoginId(memberLoginId)
			.switchIfEmpty(Mono.defer(
				() -> Mono.error(new MemberNotFoundException(memberLoginId + " 해당하는 id의 회원 정보를 찾을 수 없습니다."))));
	}

}


