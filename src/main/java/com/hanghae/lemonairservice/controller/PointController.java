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
import com.hanghae.lemonairservice.dto.point.DonateRequestDto;
import com.hanghae.lemonairservice.dto.point.DonateResponseDto;
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

	// @PostMapping("/streams/{streamId}/donations")
	// public Mono<ResponseEntity<DonateResponseDto>> donation(@PathVariable Long streamId,@RequestBody DonateRequestDto donateRequestDto,
	// 														@AuthenticationPrincipal Principal user){
	// 	return pointService.donation(streamId ,donateRequestDto,PrincipalUtil.getMember(user).getLoginId());
	//
	// }

	@PostMapping("/point")
	public Mono<ResponseEntity<PointResponseDto>> addPoint(@RequestBody AddPointRequestDto addPointRequestDto,
															@AuthenticationPrincipal Principal user){
		return pointService.addpoint(addPointRequestDto,PrincipalUtil.getMember(user));
	}
}
