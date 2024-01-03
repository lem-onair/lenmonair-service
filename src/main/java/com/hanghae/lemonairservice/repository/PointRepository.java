package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Point;

import reactor.core.publisher.Mono;

public interface PointRepository extends ReactiveCrudRepository<Point, Long> {
	Mono<Point> findByMemberId(Long id);
}