package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.FactoryBasedNavigableListAssert.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hanghae.lemonairservice.dto.point.AddPointRequestDto;
import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.dto.point.PointResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.entity.PointLog;
import com.hanghae.lemonairservice.repository.PointLogRepository;
import com.hanghae.lemonairservice.repository.PointRepository;

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
	void addPoint(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(0).build();
		AddPointRequestDto addPointRequestDto1 = AddPointRequestDto.builder().point(50).build();

		given(pointRepository.findByMemberId(member1.getId())).willReturn(Mono.just(point1));
		given(pointRepository.save(point1.addPoint(addPointRequestDto1.getPoint()))).willReturn(Mono.just(point1));
		System.out.println("2"+point1.getPoint());
		StepVerifier.create(pointService.addpoint(addPointRequestDto1,member1))
			.expectNextMatches(detail->{
				System.out.println("1"+detail.getBody().getPoint());
				assertThat(detail.getBody().getPoint()).isEqualTo(100);
				return true;
			}).verifyComplete();

		verify(pointRepository).findByMemberId(member1.getId());
		verify(pointRepository).save(point1.addPoint(addPointRequestDto1.getPoint()));
	}

	@Test
	void usePoint(){
		Member member1 = Member.builder().id(1L).email("kangminbeom@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(1000).build();
		DonationRequestDto donationRequestDto1 = DonationRequestDto.builder().donatePoint(100).contents("힘내세요").build();
		Member member2 = Member.builder().id(2L).email("kangminbeom1@gmail.com").password("Rkdalsqja1!")
			.loginId("kangminbeom1").nickname("kangminbeom1").streamKey("12345").build();
		Point point2 = Point.builder().id(2L).memberId(2L).nickname("kangminbeom1").point(1000).build();
		PointLog pointlog1 = PointLog.builder().id(1L).streamerId(2L).donaterId(1L).contents("힘내세요").donated_At(LocalDateTime.now()).donatePoint(100).build();


		given(pointRepository.findById(member1.getId())).willReturn(Mono.just(point1));
		// given(pointRepository.save(point1.usePoint(donationRequestDto1.getDonatePoint()))).willReturn(Mono.just(point1));
		given(pointRepository.findById(member2.getId())).willReturn(Mono.just(point2));
		given(pointRepository.save(point2.addPoint(donationRequestDto1.getDonatePoint()))).willReturn(Mono.just(point2));
		given(pointLogRepository.save(new PointLog(member1,donationRequestDto1, LocalDateTime.now(),member2.getId()))).willReturn(Mono.just(pointlog1));


		StepVerifier.create(pointService.usePoint(donationRequestDto1,member1,member2.getId()))
			.expectNextMatches(point ->{
				assertThat(point.getBody().getRemainingPoint()).isEqualTo(900);
				return true;
			}).verifyComplete();

		verify(pointRepository).findById(member1.getId());
		// verify(pointRepository).save(point1.usePoint(donationRequestDto1.getDonatePoint()));
		verify(pointRepository).findById(member2.getId());
		verify(pointRepository).save(point2.addPoint(donationRequestDto1.getDonatePoint()));
		verify(pointLogRepository).save(new PointLog(member1,donationRequestDto1, LocalDateTime.now(),member2.getId()));

	}


}
