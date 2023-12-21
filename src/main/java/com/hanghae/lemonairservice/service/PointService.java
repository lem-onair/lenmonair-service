package com.hanghae.lemonairservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PointService {
	private final PointRepository pointRepository;

	public Mono<ResponseEntity<PointResponseDto>> addpoint(AddPointRequestDto addPointRequestDto, Member member) {
		System.out.println("POINT: " + addPointRequestDto.getPoint());
		return pointRepository.findById(member.getId())
			.switchIfEmpty(Mono.error(new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")))
			.flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint()))
				.onErrorResume(exception -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"point 추가 오류")))
				.map(savedPoint -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint()))));
	}

	// public Mono<ResponseEntity<DonateResponseDto>> donation(Long streamId,DonateRequestDto donateRequestDto, Long donaterId) {
	// 	return pointRepository.findById(donaterId)
	// }
}