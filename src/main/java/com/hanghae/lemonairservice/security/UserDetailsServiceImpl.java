package com.hanghae.lemonairservice.security;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public Mono<UserDetails> findByUsername(String loginId) {
		return memberRepository.findByLoginId(loginId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("사용자를 찾을 수 없습니다.")))
			.map(member -> new UserDetailsImpl(member, member.getLoginId()));
	}
}
