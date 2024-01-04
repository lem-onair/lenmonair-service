package com.hanghae.lemonairservice.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.jwt.JwtTokenSubjectDto;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@InjectMocks
	MemberService memberService;

	@Mock
	MemberRepository memberRepository;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	RefreshTokenRepository refreshTokenRepository;


	@Test
	void SignUpTest(){
		SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder().email("kangminbeom@gmail.com").password("kangminbeom1!")
			.password2("kangminbeom1!").loginId("kangminbeom").nickname("kangminbeom").build();

		given(memberRepository.existsByEmail(signUpRequestDto.getEmail())).willReturn(Mono.just(false));
		given(memberRepository.existsByNickname(signUpRequestDto.getNickname())).willReturn(Mono.just(false));
		given(memberRepository.existsByLoginId(signUpRequestDto.getLoginId())).willReturn(Mono.just(false));
		given(memberRepository.save(signUpRequestDto)).willReturn(Mono.just(false));


	}
}
