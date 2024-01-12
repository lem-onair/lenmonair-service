package com.hanghae.lemonairservice.service;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.exception.stream.StreamStartedAtIsNullException;
import com.hanghae.lemonairservice.exception.stream.StreamKeysNotEqualException;
import com.hanghae.lemonairservice.exception.stream.ChannelNotExistsException;
import com.hanghae.lemonairservice.exception.stream.MemberNotFoundByLoginIdException;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class StreamingServiceTest {

	@InjectMocks
	StreamService streamService;

	@Mock
	MemberChannelRepository memberChannelRepository;

	@Mock
	MemberRepository memberRepository;


	@Test
	void checkStreamValiditySucccessTest(){
		String streamerId = "kangminbeom";
		StreamKeyRequestDto streamKeyRequestDto = StreamKeyRequestDto.builder().streamKey("1234").build();
		Member member1 = Member.builder().loginId("kangminbeom").streamKey("1234").build();
		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));

		StepVerifier.create(streamService.checkStreamValidity(streamerId,streamKeyRequestDto))
			.expectNext(true)
			.verifyComplete();

		verify(memberRepository).findByLoginId(streamerId);
	}

	@Test
	void checkStreamValidityNotEqualsStreamKeysException(){
		String streamerId = "kangminbeom";
		StreamKeyRequestDto streamKeyRequestDto = StreamKeyRequestDto.builder().streamKey("1234").build();
		Member member1 = Member.builder().loginId("kangminbeom").streamKey("12345").build();
		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));

		StepVerifier.create(streamService.checkStreamValidity(streamerId,streamKeyRequestDto))
			.verifyError(StreamKeysNotEqualException.class);
	}

	@Test
	void StartStreamSuccessTest(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();
		MemberChannel memberChannel1 = MemberChannel.builder().onAir(true).startedAt(LocalDateTime.now()).build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.just(memberChannel1));
		given(memberChannelRepository.save(memberChannel1)).willReturn(Mono.just(memberChannel1));

		StepVerifier.create(streamService.startStream(streamerId))
			.expectNext(true)
			.verifyComplete();

		verify(memberRepository).findByLoginId(streamerId);
		verify(memberChannelRepository).findByMemberId(member1.getId());
		verify(memberChannelRepository).save(memberChannel1);

	}


	@Test
	void StartStreamThrowsNotExistsIdException(){
		String streamerId = "kangminbeom";

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.empty());
		StepVerifier.create(streamService.startStream(streamerId))
			.verifyError(MemberNotFoundByLoginIdException.class);
	}

	@Test
	void StartStreamThrowsNotExistsChannelException(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.empty());

		StepVerifier.create(streamService.startStream(streamerId))
			.verifyError(ChannelNotExistsException.class);
	}

	@Test
	void stopStreamSuccessTest(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();
		MemberChannel memberChannel1 = MemberChannel.builder().onAir(true).build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.just(memberChannel1));
		given(memberChannelRepository.save(memberChannel1)).willReturn(Mono.just(memberChannel1));

		StepVerifier.create(streamService.startStream(streamerId))
			.expectNext(true)
			.verifyComplete();

		verify(memberRepository).findByLoginId(streamerId);
		verify(memberChannelRepository).findByMemberId(member1.getId());
		verify(memberChannelRepository).save(memberChannel1);
	}

	@Test
	void stopStreamThrowsNotExistsIdException() {
		String streamerId = "kangminbeom";

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.empty());

		StepVerifier.create(streamService.stopStream(streamerId))
			.verifyError(MemberNotFoundByLoginIdException.class);
	}

	@Test
	void stopStreamThrowsNotExistsChannelException(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.empty());

		StepVerifier.create(streamService.stopStream(streamerId))
			.verifyError(ChannelNotExistsException.class);
	}

	@Test
	void stopStreamNoStartedAtLogException(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();
		MemberChannel memberChannel1 = MemberChannel.builder().startedAt(null).build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.just(memberChannel1));

		StepVerifier.create(streamService.stopStream(streamerId))
			.verifyError(StreamStartedAtIsNullException.class);
	}

}
