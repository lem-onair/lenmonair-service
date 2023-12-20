package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.entity.Point;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PointRepository extends ReactiveCrudRepository<Point, Long> {
}
