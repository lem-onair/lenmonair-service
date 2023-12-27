package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Follow;

import reactor.core.publisher.Mono;

public interface FollowRepository extends ReactiveCrudRepository<Follow,Long> {
	Mono<Boolean> existsByMemberIdAndStreamerId(Long id, Long streamerId);

	Mono<Follow> findByMemberIdAndStreamerId(Long id, Long streamerId);
}