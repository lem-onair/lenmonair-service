package com.hanghae.lemonairservice.service;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.member.EmailAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.MemberIdAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.NicknameAlreadyExistException;
import com.hanghae.lemonairservice.exception.member.PasswordRetypeMismatchException;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final MemberChannelRepository memberChannelRepository;
	private final JwtUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PointRepository pointRepository;

	@Transactional
	public Mono<SignUpResponseDto> signup(SignUpRequestDto signupRequestDto) {
		return Mono.zip(passwordRetypeCheck(signupRequestDto),
			memberRepository.existsByEmail(signupRequestDto.getEmail()),
			memberRepository.existsByNickname(signupRequestDto.getNickname()),
			memberRepository.existsByLoginId(signupRequestDto.getLoginId())).flatMap(tuple -> {
			if (tuple.getT1()) {
				return Mono.defer(() -> Mono.error(new PasswordRetypeMismatchException()));
			} else if (tuple.getT2()) {
				return Mono.defer(() -> Mono.error(new EmailAlreadyExistException(signupRequestDto.getEmail())));
			} else if (tuple.getT3()) {
				return Mono.defer(() -> Mono.error(new NicknameAlreadyExistException(signupRequestDto.getNickname())));
			} else if (tuple.getT4()) {
				return Mono.defer(() -> Mono.error(new MemberIdAlreadyExistException(signupRequestDto.getLoginId())));
			} else {
				return saveMember(signupRequestDto).flatMap(saveMember -> {
					saveMemberChannel(saveMember).subscribe();
					return Mono.just(new SignUpResponseDto(saveMember.getStreamKey()));
				});
			}
		});
	}

	public Mono<Boolean> passwordRetypeCheck(SignUpRequestDto signUpRequestDto) {
		return Mono.fromCallable(() -> signUpRequestDto.getPassword().equals(signUpRequestDto.getPassword2()));
	}

	@NotNull
	private Mono<MemberChannel> saveMemberChannel(Member membermono) {
		return memberChannelRepository.save(new MemberChannel(membermono))
			.publishOn(Schedulers.boundedElastic())
			.onErrorResume(throwable -> Mono.error(
				new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "채널 생성에 실패했습니다.")));
	}

	@NotNull
	private Mono<Member> saveMember(SignUpRequestDto signupRequestDto) {
		return memberRepository.save(
				new Member(signupRequestDto, passwordEncoder.encode(signupRequestDto.getPassword()),
					UUID.randomUUID().toString()))
			.publishOn(Schedulers.boundedElastic())
			.onErrorResume(throwable -> Mono.error(
				new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다.")));
	}

	public Mono<ResponseEntity<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
		return memberRepository.findByLoginId(loginRequestDto.getLoginId()).flatMap(member -> {
			if (passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
				return jwtUtil.createAccessToken(member.getLoginId(), member.getNickname())
					.flatMap(accessToken -> jwtUtil.createRefreshToken(member.getLoginId(), member.getNickname())
						.flatMap(
							refreshToken -> refreshTokenRepository.saveRefreshToken(member.getLoginId(), refreshToken)
								.thenReturn(
									ResponseEntity.ok().body(new LoginResponseDto(accessToken, refreshToken)))));

			} else {
				return Mono.error(new RuntimeException("비밀번호가 잘못되었습니다."));
			}
		}).switchIfEmpty(Mono.error(new RuntimeException("아이디가 잘못되었습니다.")));
	}

	public Mono<ResponseEntity<String>> logout(String loginId) {
		return refreshTokenRepository.deleteByLoginId(loginId)
			.flatMap(logout -> Mono.just(ResponseEntity.ok("로그아웃되었습니다.")));
	}

}


