package com.hanghae.lemonairservice.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointService {
	private final PointRepository pointRepository;
	private final PointLogRepository pointLogRepository;
	private final RedissonClient redissonClient;
	private int user = 1;
	private static final int WAIT_TIME = 5;
	private static final int LEASE_TIME = 2;

	public Mono<ResponseEntity<PointResponseDto>> addpoint(AddPointRequestDto addPointRequestDto, Member member) {
		return pointRepository.findById(member.getId())
			.switchIfEmpty(Mono.error(new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")))
			.flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint()))
				.onErrorResume(exception -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"point 추가 오류")))
				.map(savedPoint -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint()))));
	}

	// @Transactional
	public Mono<ResponseEntity<DonationResponseDto>> usePoint(DonationRequestDto donationRequestDto, Member member, Long streamerId) {
		return Mono.defer(() -> {
			String lockKey = "LOCK" + member.getId();
			final RLock lock = redissonClient.getLock(lockKey);

			try {
				if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
					log.info(member.getLoginId() + " lock 획득 실패");
					log.info(Thread.currentThread() + " lock 획득 실패");
					throw new RuntimeException("락 획득 실패!");
				}
				log.info(member.getLoginId() + " 락 획득에 성공하였습니다.");
				log.info(Thread.currentThread() + " lock 획득 성공");

				return pointRepository.findById(member.getId())
					.flatMap(donater -> {
						if (Objects.equals(streamerId, donater.getId())) {
							return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 방송에 후원하실 수 없습니다."));
						}
						if (donater.getPoint() <= 0 || donater.getPoint() - donationRequestDto.getDonatePoint() < 0) {
							return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "후원 할 수 있는 금액이 부족합니다."));
						}

						return pointRepository.save(donater.usePoint(donationRequestDto.getDonatePoint()))
							.flatMap(savedPoint -> pointRepository.findById(streamerId))
							.flatMap(savedstreamerPoint -> {
								Point streamerPoint = savedstreamerPoint.addPoint(donationRequestDto.getDonatePoint());
								return pointRepository.save(streamerPoint)
									.flatMap(updatepoint -> {
										PointLog pointLog = new PointLog(member, donationRequestDto, LocalDateTime.now(), streamerId);
										return pointLogRepository.save(pointLog)
											.then(Mono.just(ResponseEntity.ok(new DonationResponseDto(
												member.getId(),
												member.getNickname(),
												streamerId,
												donationRequestDto.getContents(),
												donater.getPoint(),
												LocalDateTime.now().toString()
											))));
									});
							});
					});
			} catch (InterruptedException e) {
				log.info(member.getLoginId() + " catch 발생");
				log.info(Thread.currentThread() + " lock catch 발생");
				throw new RuntimeException(e.getMessage());
			} finally {
				if (lock.isHeldByCurrentThread()) {
					lock.unlock();
					log.info(member.getLoginId() + " lock 반납");
					log.info(Thread.currentThread() + " lock 반납");
				}
			}
		}).subscribeOn(Schedulers.single());
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
						return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"hello"));
					} else {
						return Mono.just(ResponseEntity.ok(Flux.fromIterable(donationRankDto)));
					}
				});
		}
}