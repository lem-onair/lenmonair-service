package com.hanghae.lemonairservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.PointLog;

public interface PointLogRepository extends ReactiveCrudRepository<PointLog,Long> {
}
