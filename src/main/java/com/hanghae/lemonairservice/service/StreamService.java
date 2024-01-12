package com.hanghae.lemonairservice.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.exception.stream.StreamStartedAtIsNullException;
import com.hanghae.lemonairservice.exception.stream.StreamKeysNotEqualException;
import com.hanghae.lemonairservice.exception.stream.ChannelNotExistsException;
import com.hanghae.lemonairservice.exception.stream.MemberNotFoundByLoginIdException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {
	private final MemberChannelRepository memberChannelRepository;
	private final MemberRepository memberRepository;
	private LocalDateTime endedAt;
	private LocalDateTime startedAt;

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		log.info(streamKey.getStreamKey());
		return memberRepository.findByLoginId(streamerId)
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(Mono.error(new StreamKeysNotEqualException()))
			.thenReturn(true);
	}

	public Mono<Boolean> startStream(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.switchIfEmpty(Mono.error(new MemberNotFoundByLoginIdException(streamerId)))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new ChannelNotExistsException()))
				.flatMap(memberChannel -> {
					memberChannel.setOnAir(true);
					memberChannel.setStartedAt(LocalDateTime.now());
					return memberChannelRepository.save(memberChannel).thenReturn(true);
				}));
	}

	public Mono<Boolean> stopStream(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.switchIfEmpty(Mono.error(new MemberNotFoundByLoginIdException(streamerId)))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new ChannelNotExistsException()))
				.flatMap(memberChannel -> {
					memberChannel.setOnAir(false);
					startedAt = memberChannel.getStartedAt();
					if (startedAt == null) {
						return Mono.error(new StreamStartedAtIsNullException());
					}
					endedAt = LocalDateTime.now();
					Duration duration = Duration.between(startedAt, endedAt);
					long minutesDifference = duration.toMinutes();
					memberChannel.setStartedAt(null);
					memberChannel.addTime((int)minutesDifference);
					return memberChannelRepository.save(memberChannel).thenReturn(true);
				}));
	}
}
