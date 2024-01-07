package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import com.hanghae.lemonairservice.dto.stream.StreamKeyRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
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
	void checkStreamValidityFailedTest(){
		String streamerId = "kangminbeom";
		StreamKeyRequestDto streamKeyRequestDto = StreamKeyRequestDto.builder().streamKey("1234").build();
		Member member1 = Member.builder().loginId("kangminbeom").streamKey("12345").build();
		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));

		StepVerifier.create(streamService.checkStreamValidity(streamerId,streamKeyRequestDto))
			.expectErrorMatches(streamkey -> streamkey instanceof RuntimeException &&
				streamkey.getMessage().contains("스트림 키가 일치하지 않습니다.")).verify();
		verify(memberRepository).findByLoginId(streamerId);
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
	void StartStreamFailedTest1(){
		String streamerId = "kangminbeom";

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.empty());
		StepVerifier.create(streamService.startStream(streamerId))
			.expectErrorMatches(stream -> stream instanceof RuntimeException &&
				stream.getMessage().contains("방송시작요청 멤버조회실패 kangminbeom는 가입되지 않은 아이디입니다.")
				).verify();

		verify(memberRepository).findByLoginId(streamerId);
	}

	@Test
	void StartStreamFailedTest2(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.empty());

		StepVerifier.create(streamService.startStream(streamerId))
			.expectErrorMatches(stream -> stream instanceof RuntimeException &&
				stream.getMessage().contains("해당 멤버의 채널이 존재하지 않습니다.")
			).verify();

		verify(memberRepository).findByLoginId(streamerId);
		verify(memberChannelRepository).findByMemberId(member1.getId());
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
	void stopStreamFailedTest1(){
		String streamerId = "kangminbeom";

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.empty());

		StepVerifier.create(streamService.stopStream(streamerId))
			.expectErrorMatches(stop -> stop instanceof RuntimeException &&
				stop.getMessage().contains("방송종료 요청 멤버조회실패 kangminbeom는 가입되지 않은 아이디입니다.")).verify();

		verify(memberRepository).findByLoginId(streamerId);
	}

	@Test
	void stopStreamFailedTest2(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.empty());

		StepVerifier.create(streamService.stopStream(streamerId))
			.expectErrorMatches(stop -> stop instanceof RuntimeException &&
				stop.getMessage().contains("해당 멤버의 채널이 존재하지 않습니다.")).verify();

		verify(memberRepository).findByLoginId(streamerId);
		verify(memberChannelRepository).findByMemberId(member1.getId());
	}

	@Test
	void stopStreamFailedTest3(){
		String streamerId = "kangminbeom";
		Member member1 = Member.builder().id(1L).loginId("kangminbeom").streamKey("12345").build();
		MemberChannel memberChannel1 = MemberChannel.builder().startedAt(null).build();

		given(memberRepository.findByLoginId(streamerId)).willReturn(Mono.just(member1));
		given(memberChannelRepository.findByMemberId(member1.getId())).willReturn(Mono.just(memberChannel1));

		StepVerifier.create(streamService.stopStream(streamerId))
			.expectErrorMatches(stop -> stop instanceof RuntimeException &&
				stop.getMessage().contains("시작되지 않은 방송입니다.")).verify();

		verify(memberRepository).findByLoginId(streamerId);
		verify(memberChannelRepository).findByMemberId(member1.getId());
	}
}
