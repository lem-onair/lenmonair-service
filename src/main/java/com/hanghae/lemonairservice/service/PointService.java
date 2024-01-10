package com.hanghae.lemonairservice.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.exception.point.FailedAddPointException;
import com.hanghae.lemonairservice.exception.point.NoDonationLogException;
import com.hanghae.lemonairservice.exception.point.NotEnoughPointException;
import com.hanghae.lemonairservice.exception.point.NotExistUserException;
import com.hanghae.lemonairservice.exception.point.NotUsepointToSelfException;
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
			.switchIfEmpty(Mono.error(new NotExistUserException()))
			.flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint()))
				.onErrorResume(exception -> Mono.error(new FailedAddPointException()))
				.map(savedPoint -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint()))));
	}

	@Transactional
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return pointRepository.findById(member.getId())
			.flatMap(donater -> {
				if (Objects.equals(streamerId, donater.getId())) {
					return Mono.error(NotUsepointToSelfException::new);
				}
				if (donater.getPoint() <= 0) {
					return Mono.error(NotEnoughPointException::new);
				}
				if (donater.getPoint() - donationRequestDto.getDonatePoint() < 0) {
					return Mono.error(NotEnoughPointException::new);
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
		return pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(member.getId())
			.flatMap(userId -> pointRepository.findById(userId))
			.map(point -> {
				String nickname = (point != null) ? point.getNickname() : null;
				return new DonationRankingDto(nickname);
			})
			.collectList()
			.flatMap(donationRankDto -> {
				if (donationRankDto == null || donationRankDto.isEmpty()) {
					return Mono.error(new NoDonationLogException());
				} else {
					return Mono.just(ResponseEntity.ok(Flux.fromIterable(donationRankDto)));
				}
			});
	}
}