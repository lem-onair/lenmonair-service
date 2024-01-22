package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.channel.BroadcastAlreadyStartedException;
import com.hanghae.lemonairservice.exception.channel.BroadcastNotStartedException;
import com.hanghae.lemonairservice.exception.channel.StreamKeyMismatchException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {
	private final MemberChannelService memberChannelService;
	private final MemberService memberService;

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		return memberService.findByLoginId(streamerId)
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(Mono.error(StreamKeyMismatchException::new))
			.thenReturn(true);
	}

	public Mono<Boolean> startStream(String streamerId) {
		return memberService.findByLoginId(streamerId)
			.flatMap(member -> memberChannelService.findByMemberId(member.getId()))
			.filter(memberChannel -> !memberChannel.getOnAir())
			.switchIfEmpty(Mono.defer(() -> Mono.error(new BroadcastAlreadyStartedException(streamerId))))
			.doOnNext(MemberChannel::onAir)
			.flatMap(memberChannelService::save)
			.thenReturn(true);
	}

	public Mono<Boolean> stopStream(String streamerId) {
		return memberService.findByLoginId(streamerId)
			.flatMap(member -> memberChannelService.findByMemberId(member.getId()))
			.filter(MemberChannel::getOnAir)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new BroadcastNotStartedException(streamerId))))
			.doOnNext(memberChannel -> {
				if (memberChannel.getStartedAt() == null) {
					log.error(memberChannel.getId() + " 방송의 startedAt의 값이 null이였습니다.");
				}
				memberChannel.offAir();
			})
			.flatMap(memberChannelService::save)
			.thenReturn(true);
	}

}
