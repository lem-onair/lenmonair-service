package com.hanghae.lemonairservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.point.DonateRequestDto;
import com.hanghae.lemonairservice.dto.point.DonateResponseDto;
import com.hanghae.lemonairservice.service.PointService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointController {
	private final PointService pointService;

	public Mono<ResponseEntity<DonateResponseDto>> donation(@RequestBody DonateRequestDto donateRequestDto){

	}
}
