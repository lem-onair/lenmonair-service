package com.hanghae.lemonairservice.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.PointLog;

import io.lettuce.core.dynamic.annotation.Param;
import reactor.core.publisher.Flux;

public interface PointLogRepository extends ReactiveCrudRepository<PointLog,Long> {
	@Query("SELECT donater_id FROM point_log WHERE streamer_id = :streamerId GROUP BY donater_id ORDER BY SUM(donate_point) DESC LIMIT 10")
	Flux<Long> findByStreamerId(@Param("streamerId") Long streamerId);
}
