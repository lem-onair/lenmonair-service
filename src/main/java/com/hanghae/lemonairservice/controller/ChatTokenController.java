package com.hanghae.lemonairservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.token.ChatTokenResponseDto;
import com.hanghae.lemonairservice.security.UserDetailsImpl;
import com.hanghae.lemonairservice.service.ChatTokenService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatTokenController {

	private final ChatTokenService chatTokenService;

	@PostMapping("/auth/chat")
	public Mono<ChatTokenResponseDto> getChatToken(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return chatTokenService.getChatToken(userDetails.getMember()).map(ChatTokenResponseDto::new);
	}
}
