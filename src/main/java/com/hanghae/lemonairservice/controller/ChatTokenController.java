package com.hanghae.lemonairservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanghae.lemonairservice.dto.token.ChatTokenResponseDto;
import com.hanghae.lemonairservice.security.UserDetailsImpl;
import com.hanghae.lemonairservice.service.ChatTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChatTokenController {

	private final ChatTokenService chatTokenService;

	@PostMapping("/auth/chat")
	public Mono<ResponseEntity<ChatTokenResponseDto>> getChatToken(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		log.info("get chat token api 실행");
		return chatTokenService.getChatToken(userDetails.getMember())
			.map(ChatTokenResponseDto::new)
			.map(ResponseEntity::ok);
	}
}
