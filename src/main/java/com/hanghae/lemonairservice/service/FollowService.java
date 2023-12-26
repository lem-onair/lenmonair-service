package com.hanghae.lemonairservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.follow.FollowResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.repository.FollowRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FollowService{
	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;

	public Mono<ResponseEntity<FollowResponseDto>> follow(Long streamerId ,Member member){
		return memberRepository.findById(streamerId)
			.switchIfEmpty()

	}
}
