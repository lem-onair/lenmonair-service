package com.hanghae.lemonairservice.rtmp.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.service.StreamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/rtmp")
@RequiredArgsConstructor
@Slf4j
public class RtmpController {
	private final StreamService streamService;

	@PostMapping("/streams/{streamerId}/check")
	public Mono<Boolean> checkStreamValidity(@PathVariable String streamerId,
		@RequestBody StreamKeyRequestDto streamKey) {
		return streamService.checkStreamValidity(streamerId, streamKey);
	}

	@PostMapping("/streams/{streamerId}/ready")
	public Mono<Boolean> readyToStream(@PathVariable String streamerId) {
		return streamService.readyToStream(streamerId);
	}

	@PostMapping("/streams/{streamerId}/stop")
	public Mono<Boolean> stopStreamingFromRtmpServer(@PathVariable String streamerId) {
		return streamService.stopStreamFromRtmpServer(streamerId);
	}
}
