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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hanghae.lemonairservice.dto.channel.MemberChannelResponseDto;
import com.hanghae.lemonairservice.service.MemberChannelService;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MemberChannelController.class)
@AutoConfigureWebTestClient
class MemberChannelControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private MemberChannelService memberChannelService;

	@Test
	@WithMockUser(username = "username")
	void getChannelsByOnAirTrue() {

		//given
		List<MemberChannelResponseDto> memberChannelResponseDtoList = new ArrayList<>();
		memberChannelResponseDtoList.add(
			MemberChannelResponseDto.builder().channelId(1L).title("title").streamerNickname("nickname").build());
		memberChannelResponseDtoList.add(
			MemberChannelResponseDto.builder().channelId(2L).title("title2").streamerNickname("nickname2").build());
		Mono<List<MemberChannelResponseDto>> expectReturn = Mono.just(
			memberChannelResponseDtoList);
		given(memberChannelService.getChannelsByOnAirTrue()).willReturn(expectReturn);

		// when
		List<MemberChannelResponseDto> responseChannelList = webTestClient.get()
			.uri("/api/channels")
			.exchange()
			.expectStatus()
			.isOk()
			.expectBodyList(MemberChannelResponseDto.class)
			.returnResult()
			.getResponseBody();

		// then
		assertNotNull(responseChannelList);
		assertThat(responseChannelList.size()).isEqualTo(2);
		assertThat(responseChannelList.get(0).getTitle()).isEqualTo("title");
		assertThat(responseChannelList.get(1).getStreamerNickname()).isEqualTo("nickname2");
	}
}