package com.hanghae.lemonairservice.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.follow.FollowResponseDto;
import com.hanghae.lemonairservice.dto.point.DonationResponseDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Follow;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.repository.FollowRepository;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;
	private final MemberChannelRepository memberChannelRepository;

	public Mono<ResponseEntity<FollowResponseDto>> follow(Long streamerId, Member member) {
		return memberRepository.findById(member.getId()).log()
			.flatMap(viewer -> {
				if (Objects.equals(streamerId, viewer.getId())) {
					return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 방송에 후원하실 수 없습니다."));
				}
				return followRepository.existsByMemberIdAndStreamerId(member.getId(), streamerId)
					.flatMap(exists -> {
						if (exists) {
							return Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 팔로우한 스트리머입니다."));
						} else {
							return memberChannelRepository.findByMemberId(streamerId)
								.flatMap(streamer -> {
									MemberChannel memberChannel = streamer.addFollower();
									return memberChannelRepository.save(memberChannel)
										.flatMap(addFollower -> followRepository.save(new Follow(streamerId, member.getId())))
										.map(savedFollow -> ResponseEntity.ok(new FollowResponseDto(streamerId, member.getId())));
								});
						}
					});
			});
	}

	public Mono<ResponseEntity<String>> unfollow(Long streamerId, Member member) {
		return followRepository.existsByMemberIdAndStreamerId(member.getId(), streamerId)
			.flatMap(exists -> {
				if (exists) {
					return followRepository.findByMemberIdAndStreamerId(member.getId(), streamerId)
						.flatMap(follow -> followRepository.delete(follow)
							.thenReturn(ResponseEntity.ok("팔로우가 취소되었습니다."))
						);
				} else {
					return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("팔로우 중이 아닙니다."));
				}
			});
	}
}