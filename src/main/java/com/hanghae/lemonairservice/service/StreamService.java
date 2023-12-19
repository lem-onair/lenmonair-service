package com.hanghae.lemonairservice.service;

import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StreamService {
	private final MemberChannelRepository memberChannelRepository;
	private final MemberRepository memberRepository;

	public Mono<Boolean> checkStreamValidity(String streamerId, StreamKeyRequestDto streamKey) {
		return memberRepository.findByLoginId(streamerId)
			.filter(member -> member.getStreamKey().equals(streamKey.getStreamKey()))
			.switchIfEmpty(
				// TODO: 2023-12-19 obsstudio에 입력한 streamKey가 일치하지 않을 때 client가 알도록 하기
				Mono.error(new RuntimeException("스트림 키가 일치하지 않습니다."))
			).thenReturn(true);
	}

	public boolean startStream(String streamerId) {
		memberRepository.findByLoginId(streamerId)
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.doOnNext(memberChannel -> {
					memberChannel.setOnAir(true);
					memberChannelRepository.save(memberChannel).subscribe();
				})).subscribe();
		return true;
	}

	public boolean stopStream(String streamName, Member user) {
		String streamKey = user.getStreamKey();
		return false;
	}

	// TODO: 2023-12-19 Reactive Transactional 적용해아함
	public Mono<Boolean> startStreamRequestFromRtmpServer(String streamerId) {
		return memberRepository.findByLoginId(streamerId)
			.switchIfEmpty(Mono.error(new RuntimeException("방송시작요청 멤버조회실패" + streamerId + " 는 가입되지 않은 아이디입니다.")))
			.flatMap(member -> memberChannelRepository.findByMemberId(member.getId())
				.switchIfEmpty(Mono.error(new RuntimeException("해당 멤버의 채널이 존재하지 않습니다.")))
				.flatMap(memberChannel -> {
					memberChannel.setOnAir(true);
					return memberChannelRepository.save(memberChannel)
						.thenReturn(true);
				}));
	}
}
