package com.hanghae.lemonairservice.repository;

import java.util.List;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.entity.Point;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PointRepository extends ReactiveCrudRepository<Point, Long> {
	@Query("SELECT p.nickname FROM Point p WHERE p.donaterId = :donaterId")
	Flux<String> findNicknameByDonaterId(@Param("donaterId") Long donaterId);
}