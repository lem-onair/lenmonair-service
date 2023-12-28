package com.hanghae.lemonairservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hanghae.lemonairservice.dto.channel.MemberChannelDetailResponseDto;
import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.service.MemberChannelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberChannelController {
	private final MemberChannelService memberChannelService;

	@GetMapping("/channels")
	public Mono<ResponseEntity<List<MemberChannelResponseDto>>> getChannelsByOnAirTrue() {
		return memberChannelService.getChannelsByOnAirTrue();
	}

	@GetMapping("/channels/{channelId}")
	public Mono<ResponseEntity<MemberChannelDetailResponseDto>> getChannelDetail(
		@PathVariable("channelId") Long channelId) {
		return memberChannelService.getChannelDetail(channelId);
	}

}
