package com.hanghae.lemonairservice.repository;

import com.hanghae.lemonairservice.entity.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends ReactiveCrudRepository<Member,Long> {

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNickname(String nickname);

    Mono<Member> findByLoginId(String userId);
  
    Mono<Boolean> existsByLoginId(String loginId);
}
