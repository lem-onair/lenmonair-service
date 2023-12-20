package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;

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
}
