package com.hanghae.lemonairservice.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.channel.MemberChannelDetailResponseDto;
import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberChannelService {

	private final AwsService awsService;
	private final MemberChannelRepository memberChannelRepository;
	private final MemberRepository memberRepository;
	private final ChatTokenService chatTokenService;

	public Mono<MemberChannel> createChannel(Member member) {
		return memberChannelRepository.save(new MemberChannel(member))
			.onErrorResume(exception -> Mono.error(new RuntimeException("user의 channel 생성 오류")));
	}

	public Mono<ResponseEntity<List<MemberChannelResponseDto>>> getChannelsByOnAirTrue() {
		return memberChannelRepository.findAllByOnAirIsTrue()
			// 2-1. 기존 코드 : 에러 발생 상황 인식에 대해서 에러메세지에 의존하며, Exception handling이 까다롭습니다.
			// .switchIfEmpty(Mono.error(new NotFoundException("현재 진행중인 방송이 없습니다.")))
			// 2-2. 개선된 코드 :  개발자가 예외 발생 상황만 보고도 어떤 오류발생상황인지 알도록 Custom Exception Class를 Naming합니다.
			.switchIfEmpty(Mono.error(new NoOnAirChannelException()))
			// 3. Custom Exception Class의 구조를 확인하세요
			.flatMap(this::convertToMemberChannelResponseDto)
			.collectList()
			.map(ResponseEntity::ok);
	}

	public Mono<ResponseEntity<MemberChannelDetailResponseDto>> getChannelDetail(Long channelId) {
		return memberChannelRepository.findById(channelId)
			.switchIfEmpty(Mono.error(new RuntimeException("해당 방송이 존재하지 않습니다.")))
			.filter(MemberChannel::getOnAir)
			.switchIfEmpty(Mono.error(new RuntimeException("해당 방송은 종료되었습니다.")))
			.flatMap(this::convertToMemberChannelDetailResponseDto)
			.map(ResponseEntity::ok);
	}

	private Mono<MemberChannelResponseDto> convertToMemberChannelResponseDto(MemberChannel memberChannel) {
		return memberRepository.findById(memberChannel.getMemberId())
			.doOnNext(memberChannel::setMember)
			.map(member -> new MemberChannelResponseDto(memberChannel,
				awsService.getThumbnailCloudFrontUrl(member.getLoginId())));
	}

	private Mono<MemberChannelDetailResponseDto> convertToMemberChannelDetailResponseDto(MemberChannel memberChannel) {
		return memberRepository.findById(memberChannel.getMemberId())
			.doOnNext(memberChannel::setMember)
			.flatMap(member -> {
				Mono<String> chatTokenMono = chatTokenService.getChatToken(member);
				return chatTokenMono.map(chatToken -> new MemberChannelDetailResponseDto(memberChannel,
					awsService.getM3U8CloudFrontUrl(member.getLoginId()), chatToken));
			});
	}
}
