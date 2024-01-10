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
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.security.PrincipalUtil;
import com.hanghae.lemonairservice.security.UserDetailsImpl;
import com.hanghae.lemonairservice.service.PointService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PointController {
	private final PointService pointService;

	@PostMapping("/point")
	public Mono<ResponseEntity<PointResponseDto>> addPoint(@RequestBody AddPointRequestDto addPointRequestDto,
		Principal user) {
		log.info("point :" + addPointRequestDto.getPoint());
		return pointService.addPoint(addPointRequestDto, PrincipalUtil.getMember(user))
			.map(ResponseEntity::ok);
	}

	@PostMapping("/{streamerId}/donations")
	public Mono<ResponseEntity<String>> usePoint(@PathVariable Long streamerId,
		@RequestBody DonationRequestDto donationRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		return pointService.usePoint(donationRequestDto, userDetails.getMember(), streamerId)
			.doOnError(
				error -> Mono.just(ResponseEntity.internalServerError().body("후원에 오류가 발생했습니다. 다시 시도해 주세요")).subscribe())
			.then(Mono.defer(() -> Mono.just(ResponseEntity.ok().body("후원 성공"))));
	}

	// @GetMapping("/donations/rank")
	// public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(@AuthenticationPrincipal Principal user) {
	// 	return pointService.donationRank(PrincipalUtil.getMember(user));
	// }

}
