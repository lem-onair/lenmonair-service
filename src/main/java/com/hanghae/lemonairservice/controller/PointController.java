package com.hanghae.lemonairservice.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.service.PointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PointController {
	private final PointService pointService;

	@GetMapping("/point")
	public Mono<ResponseEntity<PointResponseDto>> getPoint(@AuthenticationPrincipal Principal user) {
		return pointService.getPoint(PrincipalUtil.getMember(user));
	}

	@PostMapping("/point")
	public Mono<ResponseEntity<PointResponseDto>> addPoint(@RequestBody AddPointRequestDto addPointRequestDto,
		Principal user) {
		log.info("point :" + addPointRequestDto.getPoint());
		return pointService.addpoint(addPointRequestDto, PrincipalUtil.getMember(user));
	}

	@PostMapping("/{streamerId}/donations")
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(@PathVariable Long streamerId,
		@RequestBody DonationRequestDto donationRequestDto, @AuthenticationPrincipal Principal user) {
		return pointService.usePoint(donationRequestDto, PrincipalUtil.getMember(user), streamerId);
	}

	@GetMapping("/donations/rank")
	public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(@AuthenticationPrincipal Principal user) {
		return pointService.donationRank(PrincipalUtil.getMember(user));
	}

}
