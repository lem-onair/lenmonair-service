package com.hanghae.lemonairservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.channel.MemberChannelDetailResponseDto;
import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.channel.ChannelEndedException;
import com.hanghae.lemonairservice.exception.channel.ChannelNotFoundException;
import com.hanghae.lemonairservice.exception.channel.ChannelSaveFailedException;
import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberChannelService {
	private final MemberChannelRepository memberChannelRepository;
	private final MemberService memberService;

	@Value("${aws.cloudfront.domain}")
	private String cloudFrontDomain;

	public Mono<MemberChannel> createMemberChannel(Member member) {
		return memberChannelRepository.save(new MemberChannel(member))
			.onErrorResume(exception -> Mono.error(new ChannelSaveFailedException(member.getId().toString())));
	}

	public Mono<List<MemberChannelResponseDto>> getChannelsByOnAirTrue() {
		return memberChannelRepository.findAllByOnAirIsTrue()
			.switchIfEmpty(Mono.defer(() -> Mono.error(new NoOnAirChannelException())))
			.flatMap(findMemberChannel -> memberService.findById(findMemberChannel.getMemberId())
				.doOnNext(findMemberChannel::setMember)
				.then(Mono.just(new MemberChannelResponseDto(findMemberChannel,
					getThumbnailCloudFrontUrl(findMemberChannel.getMember().getLoginId())))))
			.collectList();
	}

	public Mono<MemberChannelDetailResponseDto> getChannelDetail(Long channelId) {
		return memberChannelRepository.findById(channelId)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ChannelNotFoundException("해당 방송이 존재하지 않습니다."))))
			.filter(MemberChannel::getOnAir)
			.switchIfEmpty(Mono.defer(() -> Mono.error(new ChannelEndedException("해당 방송은 종료되었습니다."))))
			.flatMap(findMemberChannel -> memberService.findById(findMemberChannel.getMemberId())
				.doOnNext(findMemberChannel::setMember)
				.then(Mono.just(new MemberChannelDetailResponseDto(findMemberChannel,
					getM3U8CloudFrontUrl(findMemberChannel.getMember().getLoginId())))));
	}

	private String getThumbnailCloudFrontUrl(String streamerLoginId) {
		String uri = String.format("/%1$s/thumbnail/%1$s_thumbnail.jpg", streamerLoginId);
		return cloudFrontDomain + uri;
	}

	private String getM3U8CloudFrontUrl(String streamerLoginId) {
		String uri = String.format("/%1$s/thumbnail/%1$s_thumbnail.jpg", streamerLoginId);
		return cloudFrontDomain + uri;
	}
}
