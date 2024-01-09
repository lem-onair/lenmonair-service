package com.hanghae.lemonairservice.service;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.el.stream.StreamELResolverImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import com.hanghae.lemonairservice.dto.point.DonationRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointLogRepository;
import com.hanghae.lemonairservice.repository.PointRepository;

import reactor.core.publisher.Mono;

@SpringBootTest
public class PointServiceTest {
	PointService pointService;
	MemberRepository memberRepository;

	PointRepository pointRepository;

	PointLogRepository pointLogRepository;

	RedissonClient redissonClient;

	@Test
	@DisplayName("동시성 제어 테스트")
	public void usePoint() throws InterruptedException {
		// given
		DonationRequestDto donationRequestDto = DonationRequestDto.builder().donatePoint(10).contents("힘내세요").build();
		Member member = Member.builder().id(1L).email("kangminbeom@gmail.com").loginId("kangminbeom").password("Kanmginbeom1!").nickname("kangminbeom").streamKey("1234").build();
		Point point1 = Point.builder().id(1L).memberId(1L).nickname("kangminbeom").point(10000).build();
		Point point2 = Point.builder().id(2L).memberId(2L).nickname("streamer").point(0).build();

		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		Long streamerId = 2L;

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					pointService.usePoint(donationRequestDto,member,streamerId);
				} finally {
					countDownLatch.countDown();
				}
			});
		}
		countDownLatch.await();

		// then
		assertThat(point2.getPoint()).isEqualTo(100);

	}
}
