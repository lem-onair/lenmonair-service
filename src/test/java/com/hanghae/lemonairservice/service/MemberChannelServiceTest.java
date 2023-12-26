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
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class) // 1. Mockito 관련 초기화, MockBean의 초기화 등등을 자동 초기화 해줍니다.
class MemberChannelServiceTest {

	// 2. 테스트하고자하는 객체입니다. 현재 우리의 케이스에서는 memberChannelService 객체가 의존하는 객체는
	// AwsService, MemberChannelRepository, MemberRepository의 3개입니다.
	// @InjectMocks 어노테이션이 붙은 이유는 의존하는 실제 객체 대신에 @Mock 어노테이션이 붙은 객체들을 Mock 객체로 주입받기 위함입니다.
	@InjectMocks
	private MemberChannelService memberChannelService;

	// 3. @Mock 어노테이션이 붙은 객체들은 테스트하고자하는 Service 객체가 사용(의존)하는 객체들이며,
	// 이들의 정상 동작 여부는 테스트 결과에 영향을 끼치지 않도록 테스트 코드를 작성합니다.
	@Mock
	private AwsService awsService;
	@Mock
	private MemberChannelRepository memberChannelRepository;
	@Mock
	private MemberRepository memberRepository;

	/**
	 * getChannelsByOnAirTrue 성공 시나리오를 테스트합니다.
	 */
	@Test
	void getChannelsByOnAirTrueSuccessTest() {
		// given
		// 4. MemberChannelService 클래스를 참고하여 수행되는 로직이 어떤 흐름인지 먼저 파악해야합니다.
		// 저희의 로직은 아래와 같습니다.
		/**
		 * 1. MemberChannelService.getChannelsByOnAirTrue() 함수가 실행된다.
		 * 2. MemberChannelRepository.findAllByOnAirIsTrue() 함수가 실행된다.
		 * 3. MemberChannelService.convertToMemberChannelResponseDto() 함수가 실행된다.
		 * 4. AwsService.getThumbnailCloudFrontUrl() 함수가 실행된다.
		 */

		// 5. 따라서 6~7에서 실제 테스트하고자하는 getChannelsByOnAirTrue() 메서드의 정상 동작 여부를 위해 아래의 데이터들을 생성합니다.
		// 6. MemberChannel : MemberChannelRepository.findAllByOnAirIsTrue() 의 리턴값 지정을 위해 필요합니다.
		MemberChannel memberChannel1 = MemberChannel.builder().id(11L).memberId(1L).onAir(true).title("title1").build();
		MemberChannel memberChannel2 = MemberChannel.builder().id(12L).memberId(2L).onAir(true).title("title2").build();

		// 7. Member : memberRepository.findById({멤버id}) 의 리턴값 지정을 위해 필요합니다.
		Member member1 = Member.builder().id(1L).email("email1").nickname("nickname1").loginId("loginId1").build();
		Member member2 = Member.builder().id(2L).email("email2").nickname("nickname2").loginId("loginId2").build();

		Flux<MemberChannel> memberChannelFlux = Flux.just(memberChannel1, memberChannel2);

		// 8. 서비스 로직에서 실행될 목객체의 응답값을 지정해줍니다.
		given(memberChannelRepository.findAllByOnAirIsTrue()).willReturn(memberChannelFlux);

		given(memberRepository.findById(memberChannel1.getMemberId())).willReturn(Mono.just(member1));
		given(memberRepository.findById(memberChannel2.getMemberId())).willReturn(Mono.just(member2));

		given(awsService.getThumbnailCloudFrontUrl(member1.getLoginId())).willReturn("mytesturl1");
		given(awsService.getThumbnailCloudFrontUrl(member2.getLoginId())).willReturn("mytesturl2");

		// 9. Mono or Flux를 구독하며 테스트하기위해서 StepVerifier.create()가 사용됩니다.
		// expectNext, expectNextMatches, verifyComplete 등의 메서드를 통해서 생성된 Mono, Flux의 데이터를 검증합니다.
		// 현재는 Mono<ResponseEntity<List<MemberChannelResponseDto>>> 를 리턴하여 Mono 안에 List가 있는 특수한 형태이므로
		// 따라서 expectNextMatches 실행시 Mono 안의 ResponseEntity<List<MemberChannelResponseDto>> 를 다룰 수 있게 됩니다.
		StepVerifier.create(memberChannelService.getChannelsByOnAirTrue()).expectNextMatches(listResponseEntity -> {
			List<MemberChannelResponseDto> body = listResponseEntity.getBody();
			// 10. expectNextMatches 안에서는 junit, jupiter 패키지의 assert~ 메서드들을 이용하여 검증한 후 모든 assert문들을 통과하면 true를 return합니다.
			assertNotNull(body);
			assertThat(body.size()).isEqualTo(2);
			assertThat(body.get(0).getTitle()).isEqualTo("title1");
			assertThat(body.get(1).getThumbnailUrl()).isEqualTo("mytesturl2");
			assertThat(body.get(1).getStreamerNickname()).isEqualTo("nickname2");
			return true;
		}).verifyComplete(); // 11. Mono or Flux로부터 모든 데이터 생성이 끝났는지 검증합니다.

		// 12. 실행되었을 것으로 기대되는 각각의 Mock 객체들의 메서드를 정확히 그 매개변수까지 명시하며 실행되었는지 확인합니다.
		verify(memberChannelRepository).findAllByOnAirIsTrue();
		verify(memberRepository).findById(memberChannel1.getMemberId());
		verify(memberRepository).findById(memberChannel2.getMemberId());
		verify(awsService).getThumbnailCloudFrontUrl(member1.getLoginId());
		verify(awsService).getThumbnailCloudFrontUrl(member2.getLoginId());
	}

	@Test
	void getChannelsByOnAirTrueThrows() {
	}
}