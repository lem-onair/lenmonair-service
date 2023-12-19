package com.hanghae.lemonairservice.rtmp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@PostMapping("/streams/{streamerId}/streaming")
	public Mono<Boolean> startStreamRequestFromRtmpServer(@PathVariable String streamerId){
		return streamService.startStreamRequestFromRtmpServer(streamerId);
	}
}
