package com.hanghae.lemonairservice.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.repository.PointLogRepository;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
	private final PointRepository pointRepository;
	private final PointLogRepository pointLogRepository;

	public Mono<ResponseEntity<PointResponseDto>> addpoint(AddPointRequestDto addPointRequestDto, Member member) {
		return pointRepository.findByMemberId(member.getId()).log()
			.switchIfEmpty(Mono.error(new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")))
			.flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint())).log()
				.onErrorResume(exception -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"point 추가 오류")))
				.map(savedPoint -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint())))).log();
	}

	@Transactional
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return pointRepository.findById(member.getId())
			.flatMap(donater -> {
				if (Objects.equals(streamerId, donater.getId())) {
					return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 방송에 후원하실 수 없습니다."));
				}
				if (donater.getPoint() <= 0) {
					return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "후원 할 수 있는 금액이 부족합니다."));
				}
				if (donater.getPoint() - donationRequestDto.getDonatePoint() < 0) {
					return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "후원 할 수 있는 금액이 부족합니다."));
				}
				return pointRepository.save(donater.usePoint(donationRequestDto.getDonatePoint()))
					.flatMap(savedPoint -> pointRepository.findById(streamerId))
					.log()
					.flatMap(savedstreamerPoint -> {
						Point streamerPoint = savedstreamerPoint.addPoint(donationRequestDto.getDonatePoint());
						return pointRepository.save(streamerPoint)
							.flatMap(updatepoint -> pointLogRepository.save(new PointLog(member,donationRequestDto,LocalDateTime.now(),streamerId)));
					}).log()
					.flatMap(updatepoint -> Mono.just(ResponseEntity.ok(new DonationResponseDto(
						member.getId(),
						member.getNickname(),
						streamerId,
						donationRequestDto.getContents(),
						donater.getPoint(),
						LocalDateTime.now().toString()
					)))).log();
			});
	}

	public Mono<ResponseEntity<Flux<DonationRankingDto>>> donationRank(Member member) {
		Flux<DonationRankingDto> donationRankDto = pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(member.getId())
			.concatMap(userId -> pointRepository.findById(userId).flux())
			.map(point -> new DonationRankingDto(point.getNickname()));

		return Mono.just(ResponseEntity.ok(donationRankDto));
	}
}