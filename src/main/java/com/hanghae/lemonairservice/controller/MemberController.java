package com.hanghae.lemonairservice.controller;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.TokenResponseDto;
import com.hanghae.lemonairservice.service.MemberService;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.security.PrincipalUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
	private final MemberService memberService;

	@PostMapping("/signup")
	public Mono<ResponseEntity<String>> signup(@RequestBody SignUpRequestDto signupRequestDto){
		return memberService.signup(signupRequestDto);
	}

	@PostMapping("/login")

	public Mono<ResponseEntity<String>> login(@RequestBody LoginRequestDto loginRequestDto){
		return memberService.login(loginRequestDto);
	}

	@PostMapping("/logout")
	public Mono<ResponseEntity<String>> logout(@AuthenticationPrincipal Principal user){
			return memberService.logout(PrincipalUtil.getMember(user).getLoginId());
	}
}
