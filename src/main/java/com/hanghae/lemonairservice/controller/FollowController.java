package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.follow.FollowRequestDto;
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
	public Mono<ResponseEntity<FollowResponseDto>> follow(Long streamerId ,@AuthenticationPrincipal Principal user){
		return followService.follow(streamerId , PrincipalUtil.getMember(user));

	}
}
