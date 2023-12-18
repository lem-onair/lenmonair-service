package com.hanghae.lemonairservice.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hanghae.lemonairservice.dto.channel.MemberChannelDetailResponseDto;
import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberChannelService {

	private final AwsService awsService;
	private final MemberChannelRepository memberChannelRepository;

	public Mono<MemberChannel> createChannel(String nickname) {
		return memberChannelRepository.save(new MemberChannel(nickname))
			.onErrorResume(exception -> Mono.error(new RuntimeException("user의 channel 생성 오류")));
	}

	public Mono<ResponseEntity<List<MemberChannelResponseDto>>> getChannelsByOnAirTrue() {
		Flux<MemberChannelResponseDto> memberChannelResponseDtoFlux = memberChannelRepository.findAllByOnAirIsTrue()
			.map(MemberChannelResponseDto::new)
			.doOnNext(this::updateThumbnailUrl);

		return memberChannelResponseDtoFlux
			.collectList()
			.map(ResponseEntity::ok);
	}

	private void updateThumbnailUrl(MemberChannelResponseDto memberChannelResponseDto) {
		memberChannelResponseDto.updateThumbnailUrl(
			awsService.getThumbnailCloudFrontUrl(memberChannelResponseDto.getStreamer()));
	}

	// TODO: 2023-12-17 postman에서만 빈 mono가 return되는건지 확인해볼 필요가 있다.
	public Mono<ResponseEntity<MemberChannelDetailResponseDto>> getChannelDetail(Long channelId) {
		return memberChannelRepository.findById(channelId)
			.switchIfEmpty(Mono.error(new RuntimeException("해당 방송이 존재하지 않습니다.")))
			.filter(MemberChannel::getOnAir)
			.switchIfEmpty(Mono.error(new RuntimeException("해당 방송은 종료되었습니다.")))
			.map(MemberChannelDetailResponseDto::new)
			.doOnNext(this::updateM3U8Url)
			.map(ResponseEntity::ok);
	}

	private void updateM3U8Url(MemberChannelDetailResponseDto memberChannelDetailResponseDto) {
		memberChannelDetailResponseDto.updateHlsUrl(
			awsService.getM3U8CloudFrontUrl(memberChannelDetailResponseDto.getStreamer()));
	}
}
