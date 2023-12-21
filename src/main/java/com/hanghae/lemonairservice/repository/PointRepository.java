package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Point;

public interface PointRepository extends ReactiveCrudRepository<Point, Long> {
}