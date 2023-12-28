package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatTokenService {

	private final JwtUtil jwtUtil;

	public Mono<String> getChatToken(Member member) {
		return jwtUtil.createChatToken(member.getLoginId(), member.getNickname()).log();
	}
}
