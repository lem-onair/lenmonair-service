package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRankingDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.exception.point.FailedAddPointException;
import com.hanghae.lemonairservice.exception.point.NoDonationLogException;
import com.hanghae.lemonairservice.exception.point.NoPointException;
import com.hanghae.lemonairservice.exception.point.NotExistUserException;
import com.hanghae.lemonairservice.repository.PointLogRepository;
import com.hanghae.lemonairservice.repository.PointRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

	@InjectMocks
	PointService pointService;

	@Mock
	PointRepository pointRepository;
	@Mock
	PointLogRepository pointLogRepository;

	@Test
	void addPointSuccessTest(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(0).build();
		AddPointRequestDto addPointRequestDto1 = AddPointRequestDto.builder().point(50).build();

		given(pointRepository.findByMemberId(member1.getId())).willReturn(Mono.just(point1));
		given(pointRepository.save(any())).willReturn(Mono.just(point1));
		System.out.println("2"+point1.getPoint());
		StepVerifier.create(pointService.addpoint(addPointRequestDto1,member1))
			.expectNextMatches(detail->{
				System.out.println("1"+detail.getBody().getPoint());
				assertThat(detail.getBody().getPoint()).isEqualTo(50);
				return true;
			}).verifyComplete();

		verify(pointRepository).findByMemberId(member1.getId());
		verify(pointRepository).save(point1.addPoint(addPointRequestDto1.getPoint()));
	}

	@Test
	void addPointThrowsNotExistUserException(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		AddPointRequestDto addPointRequestDto1 = AddPointRequestDto.builder().point(50).build();

		given(pointRepository.findByMemberId(member1.getId())).willReturn(Mono.empty());

		StepVerifier.create(pointService.addpoint(addPointRequestDto1,member1))
			.verifyError(NotExistUserException.class);
	}

	@Test
	void addPointThrowsFailedAddPointException(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(0).build();
		AddPointRequestDto addPointRequestDto1 = AddPointRequestDto.builder().point(0).build();

		given(pointRepository.findByMemberId(member1.getId())).willReturn(Mono.just(point1));
		given(pointRepository.save(point1.addPoint(addPointRequestDto1.getPoint()))).willReturn(Mono.error(new FailedAddPointException()));

		StepVerifier.create(pointService.addpoint(addPointRequestDto1,member1))
			.verifyError(FailedAddPointException.class);
	}

	@Test
	void usePointSuccessTest(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(1000).build();
		DonationRequestDto donationRequestDto1 = DonationRequestDto.builder().donatePoint(100).contents("힘내세요").build();
		Member member2 = Member.builder().id(2L).email("kangminbeom1@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom1").nickname("kangminbeom1").streamKey("12345").build();
		Point point2 = Point.builder().id(2L).memberId(2L).nickname("kangminbeom1").point(1000).build();
		PointLog pointlog1 = PointLog.builder().id(1L).streamerId(2L).donaterId(1L).contents("힘내세요").donated_At(LocalDateTime.now()).donatePoint(100).build();


		given(pointRepository.findById(member1.getId())).willReturn(Mono.just(point1));
		given(pointRepository.save(any())).willReturn(Mono.just(point1));
		given(pointRepository.findById(member2.getId())).willReturn(Mono.just(point2));
		given(pointRepository.save(any())).willReturn(Mono.just(point2));
		given(pointLogRepository.save(any())).willReturn(Mono.just(pointlog1));


		StepVerifier.create(pointService.usePoint(donationRequestDto1,member1,member2.getId()))
			.expectNextMatches(point ->{
				assertThat(point.getBody().getRemainingPoint()).isEqualTo(900);
				return true;
			}).verifyComplete();

		verify(pointRepository).findById(member1.getId());
		verify(pointRepository,times(2)).save(any());
		verify(pointRepository).findById(member2.getId());
		verify(pointLogRepository).save(any());
	}


	@Test
	void usePointFailedTest(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(0).build();
		DonationRequestDto donationRequestDto1 = DonationRequestDto.builder().donatePoint(100).contents("힘내세요").build();
		Member member2 = Member.builder().id(2L).email("kangminbeom1@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom1").nickname("kangminbeom1").streamKey("12345").build();


		given(pointRepository.findById(member1.getId())).willReturn(Mono.just(point1));

		StepVerifier.create(pointService.usePoint(donationRequestDto1,member1,member2.getId()))
			.expectErrorMatches(point -> point instanceof ResponseStatusException).verify();

		verify(pointRepository,never()).save(any());
		verify(pointLogRepository,never()).save(any());
	}

	@Test
	void usePointThrowsNotUsepointToSelfException(){
		DonationRequestDto donationRequestDto1 = DonationRequestDto.builder().donatePoint(100).contents("힘내세요").build();
		Member member2 = Member.builder().id(2L).email("kangminbeom1@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom1").nickname("kangminbeom1").streamKey("12345").build();
		Point point2 = Point.builder().id(2L).memberId(2L).nickname("kangminbeom1").point(1000).build();


		given(pointRepository.findById(member2.getId())).willReturn(Mono.just(point2));

		StepVerifier.create(pointService.usePoint(donationRequestDto1,member2,member2.getId()))
			.expectErrorMatches(point -> point instanceof ResponseStatusException &&
				point.getMessage().contains("본인 방송에 후원하실 수 없습니다.")).verify();

		verify(pointRepository,never()).save(any());
		verify(pointLogRepository,never()).save(any());
	}

	@Test
	void usePointThrowsNoPointException(){
		DonationRequestDto donationRequestDto1 = DonationRequestDto.builder().donatePoint(100).contents("힘내세요").build();

		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();

		Member member2 = Member.builder().id(2L).email("kangminbeom1@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom1").nickname("kangminbeom1").streamKey("12345").build();

		Point point2 = Point.builder().id(2L).memberId(2L).nickname("kangminbeom1").point(0).build();

		given(pointRepository.findById(member2.getId())).willReturn(Mono.just(point2));

		StepVerifier.create(pointService.usePoint(donationRequestDto1,member2,member1.getId()))
				.verifyError(NoPointException.class);
		}

	@Test
	void donationRankSuccessTest(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("user1").streamKey("1234").build();

		// Mock 데이터 생성

		PointLog pointlog1 = PointLog.builder().id(1L).streamerId(1L).donaterId(2L).contents("힘내세요").donated_At(LocalDateTime.now()).donatePoint(100).build();

		PointLog pointlog2 = PointLog.builder().id(2L).streamerId(1L).donaterId(3L).contents("힘내세요").donated_At(LocalDateTime.now()).donatePoint(200).build();

		Point point2 = Point.builder().id(2L).memberId(2L).nickname("user2").point(200).build();
		Point point3 = Point.builder().id(3L).memberId(3L).nickname("user3").point(100).build();

		// Mock 설정
		given(pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(member1.getId()))
			.willReturn(Flux.just(pointlog1.getDonaterId(), pointlog2.getDonaterId()));
		given(pointRepository.findById(2L)).willReturn(Mono.just(point2));
		given(pointRepository.findById(3L)).willReturn(Mono.just(point3));

		StepVerifier.create(pointService.donationRank(member1))
			.expectNextMatches(rank -> {
				assertThat(rank.getStatusCode()).isEqualTo(HttpStatus.OK);

				Flux<DonationRankingDto> fluxdonationRanking = rank.getBody();
				assertThat(fluxdonationRanking).isNotNull();

				List<DonationRankingDto> listdonationRanking = fluxdonationRanking.collectList().block();
				assertThat(listdonationRanking).isNotNull();
				assertThat(listdonationRanking).hasSize(2);

				assertThat(listdonationRanking.get(0).getNickname()).isEqualTo("user2");
				assertThat(listdonationRanking.get(1).getNickname()).isEqualTo("user3");
				return true;
			}).verifyComplete();
		verify(pointLogRepository).findByStreamerIdOrderBySumOfDonateLimit10(member1.getId());
		verify(pointRepository).findById(2L);
		verify(pointRepository).findById(3L);
	}

	@Test
	void donationRankFailedTest(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("user1").streamKey("1234").build();

		// Mock 설정
		given(pointLogRepository.findByStreamerIdOrderBySumOfDonateLimit10(member1.getId())).willReturn(Flux.empty());

		StepVerifier.create(pointService.donationRank(member1))
			.verifyError(NoDonationLogException.class);
	}




}
