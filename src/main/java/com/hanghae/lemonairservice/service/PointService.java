package com.hanghae.lemonairservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final MemberRepository memberRepository;
	private final PointLogRepository pointLogRepository;

	public Mono<PointResponseDto> addPoint(AddPointRequestDto addPointRequestDto, Member member) {
		return memberRepository.save(member.addPoint(addPointRequestDto.getPoint()))
			.map(savedMember -> new PointResponseDto(savedMember.getId(), savedMember.getTotalPoint()));
	}

	@Transactional
	public Mono<Void> usePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return memberRepository.findById(streamerId)
			.filter(findStreamer -> findStreamer.getLoginId().equals(member.getLoginId()))
			.switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("본인 방송에 후원 x"))))
			.filter(findStreamer -> member.getTotalPoint() < donationRequestDto.getDonatePoint())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new RuntimeException("돈이 모자르다."))))
			.flatMap(
				findStreamer -> pointLogRepository.save(new PointLog(member, donationRequestDto, streamerId)).then());
	}

	// public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(Member member) {
	// 	Flux<DonationRankingDto> donationRankDto = pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(
	// 			member.getId())
	// 		.concatMap(userId -> pointRepository.findById(userId).flux())
	// 		.map(point -> new DonationRankingDto(point.getNickname()));
	//
	// 	return Mono.just(ResponseEntity.ok(donationRankDto));
	// }
}