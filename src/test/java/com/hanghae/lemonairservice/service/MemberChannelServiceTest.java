package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class MemberChannelServiceTest {

	@InjectMocks
	private MemberChannelService memberChannelService;
	@Mock
	private AwsService awsService;
	@Mock
	private MemberChannelRepository memberChannelRepository;
	@Mock
	private MemberRepository memberRepository;

	@Test
	void getChannelsByOnAirTrueSuccessTest() {
		// given
		MemberChannel memberChannel1 = MemberChannel.builder().id(11L).memberId(1L).onAir(true).title("title1").build();
		MemberChannel memberChannel2 = MemberChannel.builder().id(12L).memberId(2L).onAir(true).title("title2").build();

		Member member1 = Member.builder().id(1L).email("email1").nickname("nickname1").loginId("loginId1").build();
		Member member2 = Member.builder().id(2L).email("email2").nickname("nickname2").loginId("loginId2").build();

		Flux<MemberChannel> memberChannelFlux = Flux.just(memberChannel1, memberChannel2);

		given(memberChannelRepository.findAllByOnAirIsTrue()).willReturn(memberChannelFlux);

		given(memberRepository.findById(memberChannel1.getMemberId())).willReturn(Mono.just(member1));
		given(memberRepository.findById(memberChannel2.getMemberId())).willReturn(Mono.just(member2));

		given(awsService.getThumbnailCloudFrontUrl(member1.getLoginId())).willReturn("mytesturl1");
		given(awsService.getThumbnailCloudFrontUrl(member2.getLoginId())).willReturn("mytesturl2");

		// when
		StepVerifier.create(memberChannelService.getChannelsByOnAirTrue()).expectNextMatches(listResponseEntity -> {
			List<MemberChannelResponseDto> body = listResponseEntity.getBody();
			assertNotNull(body);
			assertThat(body.size()).isEqualTo(2);
			assertThat(body.get(0).getTitle()).isEqualTo("title1");
			assertThat(body.get(1).getThumbnailUrl()).isEqualTo("mytesturl2");
			assertThat(body.get(1).getStreamerNickname()).isEqualTo("nickname2");
			return true;
		}).verifyComplete();

		// then
		verify(memberChannelRepository).findAllByOnAirIsTrue();
		verify(memberRepository).findById(memberChannel1.getMemberId());
		verify(memberRepository).findById(memberChannel2.getMemberId());
		verify(awsService).getThumbnailCloudFrontUrl(member1.getLoginId());
		verify(awsService).getThumbnailCloudFrontUrl(member2.getLoginId());
	}

	@Test
	void getChannelsByOnAirTrueThrowsNoOnAirChannelException() {
		//given
		given(memberChannelRepository.findAllByOnAirIsTrue()).willReturn(Flux.empty());

		// when then
		StepVerifier.create(memberChannelService.getChannelsByOnAirTrue()).verifyError(NoOnAirChannelException.class);
	}
}