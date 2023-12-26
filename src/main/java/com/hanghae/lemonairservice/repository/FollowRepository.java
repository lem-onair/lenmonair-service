package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Follow;

public interface FollowRepository extends ReactiveCrudRepository<Follow,Long> {
}
