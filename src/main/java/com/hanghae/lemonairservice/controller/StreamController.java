package com.hanghae.lemonairservice.controller;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/streams")
@Slf4j
public class StreamController {
    private final StreamService streamService;

	@PostMapping("/{streamerId}/check")
	public Mono<Boolean> checkStreamValidity(@PathVariable String streamerId,
		@RequestBody StreamKeyRequestDto streamKey) {
		return streamService.checkStreamValidity(streamerId, streamKey);
	}

	@PostMapping("/{streamerId}/onair")
	public Mono<Boolean> startStreamRequestFromRtmpServer(@PathVariable String streamerId) {
		return streamService.startStream(streamerId);
	}

	@PostMapping("/{streamerId}/offair")
	public Mono<Boolean> stopStreamRequestFromRtmpServer(@PathVariable String streamerId) {
		return streamService.stopStream(streamerId);
	}
}
