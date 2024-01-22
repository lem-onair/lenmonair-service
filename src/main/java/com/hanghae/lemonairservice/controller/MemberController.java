package com.hanghae.lemonairservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.security.UserDetailsImpl;
import com.hanghae.lemonairservice.service.MemberService;
import com.hanghae.lemonairservice.util.ResponseMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/signup")
	public Mono<ResponseEntity<SignUpResponseDto>> signup(@RequestBody @Valid SignUpRequestDto signupRequestDto) {
		return memberService.signup(signupRequestDto).flatMap(ResponseMapper::mapToResponse);
	}

	@PostMapping("/login")
	public Mono<ResponseEntity<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
		return memberService.login(loginRequestDto).flatMap(ResponseMapper::mapToResponse);
	}

	@PostMapping("/logout")
	public Mono<ResponseEntity<String>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return memberService.logout(userDetails.getMember().getLoginId()).flatMap(ResponseMapper::mapToResponse);
	}
}
