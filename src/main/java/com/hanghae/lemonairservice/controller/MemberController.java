package com.hanghae.lemonairservice.controller;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.TokenResponseDto;
import com.hanghae.lemonairservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
	public Mono<ResponseEntity<?>> signup(@RequestBody LoginRequestDto loginRequestDto){
		return memberService.login(loginRequestDto);
	}
//
//	@PostMapping("/logout")
//	public ResponseEntity logout(@Valid @RequestBody SignUpRequestDto signupRequestDto){
//		memberService.signup(signupRequestDto);
//		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입에 성공하였습니다.");
//	}
}
