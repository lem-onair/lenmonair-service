package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
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

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		log.info(streamKey.getStreamKey());
		return memberRepository.findByLoginId(streamerId)
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(
				// TODO: 2023-12-19 obsstudio에 입력한 streamKey가 일치하지 않을 때 client가 알도록 하기
				Mono.error(new RuntimeException("스트림 키가 일치하지 않습니다.")))
			.thenReturn(true);
	}

	public Mono<Boolean> startStream(Member member) {
		return memberChannelRepository.findByMemberId(member.getId()).flatMap(memberChannel -> {
			if (memberChannel.getReady()) {
				memberChannel.setOnAir(true);
				return memberChannelRepository.save(memberChannel)
					.flatMap(savedChannel -> Mono.just(true))
					.onErrorReturn(false);
			} else {
				log.error(member.getLoginId() + " 의 방송 시작 요청이 OBS Studio로 송출되고있지 않아 실패했습니다.");
				return Mono.just(false);
			}
		});
	}

	@Transactional
	public Mono<Boolean> readyToStream(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.switchIfEmpty(Mono.error(new RuntimeException("방송시작요청 멤버조회실패" + streamerId + " 는 가입되지 않은 아이디입니다.")))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
				.flatMap(memberChannel -> {
					memberChannel.setReady(true);
					return memberChannelRepository.save(memberChannel)
						.flatMap(savedChannel -> Mono.just(true))
						.onErrorReturn(false);
				}));
	}

	@Transactional
	public Mono<Boolean> stopStream(Member member) {
		return memberChannelRepository.findByMemberId(member.getId())
			.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
			.flatMap(memberChannel -> {
				memberChannel.setOnAir(false);
				memberChannel.setReady(false);
				return memberChannelRepository.save(memberChannel)
					.flatMap(savedChannel -> Mono.just(true))
					.onErrorReturn(false);
			});
	}

	@Transactional
	public Mono<Boolean> stopStreamFromRtmpServer(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
				.flatMap(memberChannel -> {
					memberChannel.setReady(false);
					memberChannel.setOnAir(false);
					return memberChannelRepository.save(memberChannel)
						.flatMap(savedChannel -> Mono.just(true))
						.onErrorReturn(false);
				}));
	}
}
