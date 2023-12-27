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

import com.hanghae.lemonairservice.dto.channel.MemberChannelDetailResponseDto;
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
	// 5. WithMockUser 어노테이션이 없으면 security filter chain에 걸려서 항상 401 응답이 나온다.
	@WithMockUser(username = "username")
	void getChannelsByOnAirTrue() {
		// given when then
		/** given
		 테스트를 위해 주어진 상태
		 테스트 동작을 위해서 주어지는 환경과 조건을 정의

		 현재 테스트하고자하는 것은 MemberChannelController의 getChannelsByOnAirTrue() 메서드의 동작 여부 뿐이므로
		 MemberChannelService가 제대로 동작하지 않더라도, Controller가 정상 동작한다면 테스트의 결과는 통과하여야한다.
		 따라서 Service 클래스를 실제로 wire 하지 않고, MockBean으로 가짜 객체를 주입받아서 사용한다.

		 중요! "내가 Controller를 사용함으로써 실행되는 Service 클래스의 함수에 대하여 미리 응답을 정해줘야한다."
		 */
		// 6. MemberChannelService 클래스에서 가짜로 받을 응답을 getChannelsByOnAirTrue() 메서드의 리턴 형식을 참고하여 작성,
		List<MemberChannelResponseDto> memberChannelResponseDtoList = new ArrayList<>();
		memberChannelResponseDtoList.add(
			// 7. builder 패턴을 적용하여 ResponseDto를 쉽게 생성할 수 있도록 함.
			// 8. builder 패턴을 적용하기 위한 MemberChannelResponseDto의 변경사항을 확인하세요.
			MemberChannelResponseDto.builder().channelId(1L).title("title").streamerNickname("nickname").build());
		memberChannelResponseDtoList.add(
			MemberChannelResponseDto.builder().channelId(2L).title("title2").streamerNickname("nickname2").build());
		Mono<ResponseEntity<List<MemberChannelResponseDto>>> expectReturn = Mono.just(
			ResponseEntity.ok(memberChannelResponseDtoList));

		// 9. MemberChannelController의 getChannelsByOnAirTrue() 함수를 호출하는 경우 실제 비즈니스로직에서는 
		// MemberChannelService.getChannelsByOnAirTrue() 함수가 실행될 것입니다.
		// 현재 단위테스트에서는 MemberChannelService의 정상 작동 여부는 Aspect가 아니므로
		// 테스트환경에서 MemberChannelService.getChannelsByOnAirTrue()함수가 실행되는 경우의 응답값을 지정해줍니다.
		given(memberChannelService.getChannelsByOnAirTrue()).willReturn(expectReturn);

		/** when
		 * 테스트하고싶은 로직을 직접 실행하는 부분입니다. 실행하고싶은 Controller 클래스의 메소드를 실행시키도록 하는
		 * 요청 uri, 요청할때 필요한 data 등을 전달한 후 기대하는 응답이 맞는지 검사합니다.
		 */
		// when
		List<MemberChannelResponseDto> responseChannelList = webTestClient.get()
			.uri("/api/channels") // 10. 실행하고자하는 Controller의 메서드와 매핑된 요청 uri
			.exchange()
			.expectStatus()
			.isOk() // 11. expectStatus().isOk() : Controller test 간에 인증/인가 관련 예외를 테스트하는 경우가 아니라면 보통 200 응답을 기대함
			// 12. ResponseBody 안의 데이터는 MemberChannelResponseDto의 리스트 형식을 기대합니다.
			// 13. MemberChannelResponseDto에 매개변수가 없는 기본 생성자가 있어야 가능합니다. MemberChannelResponseDto 참고.
			.expectBodyList(MemberChannelResponseDto.class)
			.returnResult()
			.getResponseBody(); // 14. ResponseBody 내부의 데이터를 꺼냅니다. 현재는 responseChannelList 변수에 값을 대입합니다.

		/** then
		 * controller 함수의 반환값이 유효한지 확인합니다.
		 * 여러 패키지에서 assert~ 의 함수가 정의되어있어 import 할때 조심해야합니다.
		 * assertNotNull 메서드의 경우 import static org.junit.jupiter.api.Assertions.*;
		 * assertThat 메서드의 경우 import static org.assertj.core.api.Assertions.* 를 import 해야합니다.
		 */
		assertNotNull(responseChannelList); // 14. null-check
		// 15. 6~7번에서 정의한 내용 대로 응답이 나왔는지 체크합니다.
		assertThat(responseChannelList.size()).isEqualTo(2);
		assertThat(responseChannelList.get(0).getTitle()).isEqualTo("title");
		assertThat(responseChannelList.get(1).getStreamerNickname()).isEqualTo("nickname2");
	}
}