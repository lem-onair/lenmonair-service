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
import com.hanghae.lemonairservice.exception.channel.NoExistChannelException;
import com.hanghae.lemonairservice.exception.channel.NoOnAirChannelException;
import com.hanghae.lemonairservice.exception.channel.OffAirBroadCastException;
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
			assertThat(body.get(0).getTitle()).isEqualTo("title2");
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

	@Test
	void getChannelDetailsSuccessTest(){
		MemberChannel memberChannel1 = MemberChannel.builder().id(1L).title("안녕하세요").memberId(1L).totalStreaming(0).startedAt(null)
			.onAir(true).build();

		Member member1 = Member.builder().id(1L).email("email1").nickname("nickname1").loginId("loginId1").build();

		given(memberChannelRepository.findById(memberChannel1.getId())).willReturn(Mono.just(memberChannel1));
		given(memberRepository.findById(memberChannel1.getId())).willReturn(Mono.just(member1));
		given(awsService.getM3U8CloudFrontUrl(member1.getLoginId())).willReturn("hello1");
		StepVerifier.create(memberChannelService.getChannelDetail(memberChannel1.getId()))
			.expectNextMatches(detail->{
				assertThat(detail.getBody().getTitle()).isEqualTo("안녕하세요");
				assertThat(detail.getBody().getChannelId()).isEqualTo(1L);
				return true;
			}).verifyComplete();
		verify(memberChannelRepository).findById(memberChannel1.getId());
		verify(memberChannelRepository).findById(memberChannel1.getId());
		verify(awsService).getM3U8CloudFrontUrl(member1.getLoginId());
	}

	@Test
	void getChannelDetailsThrowsNoExistChannelException(){
		MemberChannel memberChannel1 = MemberChannel.builder().id(1L).title("안녕하세요").memberId(1L).totalStreaming(0).startedAt(null)
			.onAir(true).build();

		Long channelId = 1L;

		given(memberChannelRepository.findById(channelId)).willReturn(Mono.empty());

		StepVerifier.create(memberChannelService.getChannelDetail(channelId))
			.verifyError(NoExistChannelException.class);
	}


	@Test
	void getChannelDetailsThrowsoffAirBroadCastException(){
		MemberChannel memberChannel1 = MemberChannel.builder().id(1L).title("안녕하세요").memberId(1L).totalStreaming(0).startedAt(null)
			.onAir(false).build();

		Long channelId = 1L;

		given(memberChannelRepository.findById(channelId)).willReturn(Mono.just(memberChannel1));

		StepVerifier.create(memberChannelService.getChannelDetail(channelId))
			.verifyError(OffAirBroadCastException.class);
	}


	// @Test
	// void convertToMemberChannelResponseDtoSuccessTest(){
	// 	// Given
	// 	MemberChannel memberChannel1 = MemberChannel.builder().id(1L).title("안녕하세요").memberId(1L).totalStreaming(0).startedAt(null)
	// 		.onAir(true).build();
	//
	// 	Member member1 = Member.builder().id(1L).email("email1").nickname("nickname1").loginId("loginId1").build();
	//
	// 	given(memberRepository.findById(memberChannel1.getId())).willReturn(Mono.just(member1));
	// 	given(awsService.getThumbnailCloudFrontUrl(member1.getLoginId())).willReturn("hi");
	//
	// 	// When
	// 	StepVerifier.create(memberChannelService.convertToMemberChannelResponseDto(memberChannel1))
	// 		.expectNextMatches(dto -> {
	// 			assertTrue(dto instanceof MemberChannelResponseDto);
	// 			return true;
	// 		})
	// 		.verifyComplete();
	//
	// 	// Then
	// 	verify(memberRepository).findById(memberChannel1.getId());
	// 	verify(awsService).getThumbnailCloudFrontUrl(member1.getLoginId());
	// }

	// @Test
	// void convertToMemberChannelDetailResponseDto(){
	// 	// Given
	// 	MemberChannel memberChannel1 = MemberChannel.builder().id(1L).title("안녕하세요").memberId(1L).totalStreaming(0).startedAt(null)
	// 		.onAir(true).build();
	//
	// 	Member member1 = Member.builder().id(1L).email("email1").nickname("nickname1").loginId("loginId1").build();
	//
	// 	given(memberRepository.findById(memberChannel1.getId())).willReturn(Mono.just(member1));
	// 	given(awsService.getM3U8CloudFrontUrl(member1.getLoginId())).willReturn("hi");
	//
	// 	// When
	// 	StepVerifier.create(memberChannelService.convertToMemberChannelDetailResponseDto(memberChannel1))
	// 		.expectNextMatches(dto -> {
	// 			assertTrue(dto instanceof MemberChannelDetailResponseDto);
	// 			return true;
	// 		})
	// 		.verifyComplete();
	//
	// 	// Then
	// 	verify(memberRepository).findById(memberChannel1.getId());
	// 	verify(awsService).getM3U8CloudFrontUrl(member1.getLoginId());
	// }
}