package com.hanghae.lemonairservice.repository;

import com.hanghae.lemonairservice.entity.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends ReactiveCrudRepository<Member,Long> {
    Mono<Member> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNickname(String nickname);

    Mono<Boolean> existsByUserid(String userid);
}
