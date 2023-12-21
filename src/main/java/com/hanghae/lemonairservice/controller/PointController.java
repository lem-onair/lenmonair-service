package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.service.PointService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PointController {
	private final PointService pointService;

	@PostMapping("/point")
	public Mono<ResponseEntity<PointResponseDto>> addPoint(@RequestBody AddPointRequestDto addPointRequestDto,
		@AuthenticationPrincipal Principal user){
		return pointService.addpoint(addPointRequestDto, PrincipalUtil.getMember(user));
	}

	@PostMapping("/{streamerId}/donations")
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(@PathVariable Long streamerId, @RequestBody DonationRequestDto donationRequestDto,
		@AuthenticationPrincipal Principal user){
		return pointService.usePoint(donationRequestDto, PrincipalUtil.getMember(user),streamerId);
	}


}
