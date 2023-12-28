package com.hanghae.lemonairservice.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
	@InjectMocks
	MemberService memberService;
	@MockBean
	MemberRepository memberRepository;
	@MockBean
	PasswordEncoder passwordEncoder;
	@MockBean
	MemberChannelRepository memberChannelRepository;
	@MockBean
	JwtUtil jwtUtil;
	@MockBean
	RefreshTokenRepository refreshTokenRepository;
	@MockBean
	PointRepository pointRepository;

}