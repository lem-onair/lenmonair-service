package com.hanghae.lemonairservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.Response;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonateRequestDto;
import com.hanghae.lemonairservice.dto.point.DonateResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.repository.PointRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PointService {
	private final PointRepository pointRepository;
	public Mono<Point> createPoint(Member member) {
		return pointRepository.save(new Point(member))
			.onErrorResume(exception -> Mono.error(new RuntimeException("user의 channel 생성 오류")));
	}

	public Mono<ResponseEntity<PointResponseDto>> addpoint(AddPointRequestDto addPointRequestDto, Member member) {
		return pointRepository.findById(member.getId())
			.switchIfEmpty(Mono.error(new ResponseStatusException(
				HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다.")))
			.flatMap(point -> pointRepository.save(point.addPoint(addPointRequestDto.getPoint()))
				.map(savedPoint -> ResponseEntity.ok().body(new PointResponseDto(member, point.getPoint()))));

	}

	// public Mono<ResponseEntity<DonateResponseDto>> donation(Long streamId,DonateRequestDto donateRequestDto, Long donaterId) {
	// 	return pointRepository.findById(donaterId)
	// }
}
