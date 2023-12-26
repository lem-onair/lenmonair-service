package com.hanghae.lemonairservice.controller;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.service.MemberChannelService;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MemberChannelController.class) // 1. 어떤 컨트롤러 클래스를 테스트할건지?
@AutoConfigureWebTestClient // 2. @WithMockUser를 사용하기 위해서
class MemberChannelControllerTest {

	@Autowired
	private WebTestClient webTestClient; // 3. WebFlux 환경에서 가상의 요청을 만들 수 있는 객체

	@MockBean
	private MemberChannelService memberChannelService; // 4. 테스트하고자하는 Controller가 사용하는 객체들이 가짜 빈으로 필요합니다.

	@Test
	@WithMockUser(username = "username")
		// 5. 없으면 security filter chain에 걸려서 항상 401 응답이 나온다.
	void getChannelsByOnAirTrue() {
		/** given
		 테스트를 위해 주어진 상태
		 테스트 동작을 위해서 주어지는 환경과 조건을 정의
		 현재 테스트하고자하는 것은 MemberChannelController의 getChannelsByOnAirTrue() 메서드의 동작 여부이므로
		 MemberChannelService가 제대로 동작하지 않더라도, Controller가 정상 동작한다면 테스트의 결과는 통과하여야한다.
		 따라서 Service 함수를 실제로 wire 하지 않고, MockBean으로 가짜 객체를 주입받아서 사용한다.
		 "내가 Controller를 사용함으로써 실행될 것으로 기대되는
		*/

		// 따라서 가짜로  받을 응답을 getChannelsByOnAirTrue() 메서드의 리턴 형식을 참고하여 작성,
		List<MemberChannelResponseDto> memberChannelResponseDtoList = new ArrayList<>();
		memberChannelResponseDtoList.add(
			MemberChannelResponseDto.builder().channelId(1L).title("title").streamerNickname("nickname").build());
		memberChannelResponseDtoList.add(
			MemberChannelResponseDto.builder().channelId(2L).title("title2").streamerNickname("nickname2").build());
		Mono<ResponseEntity<List<MemberChannelResponseDto>>> expectReturn = Mono.just(
			ResponseEntity.ok(memberChannelResponseDtoList));

		given(memberChannelService.getChannelsByOnAirTrue()).willReturn(expectReturn);

		// when(memberChannelService.getChannelsByOnAirTrue()).thenReturn(expectReturn);

		// when 
		List<MemberChannelResponseDto> responseChannelList = webTestClient.get()
			.uri("/api/channels")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(MemberChannelResponseDto.class)
			.returnResult()
			.getResponseBody();

		assertNotNull(responseChannelList);
		assertThat(responseChannelList.size()).isEqualTo(2);
		assertThat(responseChannelList.get(0).getTitle()).isEqualTo("title");
		assertThat(responseChannelList.get(1).getStreamerNickname()).isEqualTo("nickname2");

	}

	@Test
	void getChannelDetail() {
	}
}