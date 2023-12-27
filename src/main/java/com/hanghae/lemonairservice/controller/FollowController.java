package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.follow.FollowResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.service.FollowService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {
	private final FollowService followService;

	@PostMapping("/{streamerId}/follow")
	public Mono<ResponseEntity<FollowResponseDto>> follow(@PathVariable Long streamerId ,@AuthenticationPrincipal Principal user){
		return followService.follow(streamerId , PrincipalUtil.getMember(user));
	}

	@PostMapping("/{streamerId}/unfollow")
	public Mono<ResponseEntity<String>> unfollow(@PathVariable Long streamerId ,@AuthenticationPrincipal Principal user){
		return followService.unfollow(streamerId ,PrincipalUtil.getMember(user));
	}
}
