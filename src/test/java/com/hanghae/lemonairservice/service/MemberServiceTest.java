package com.hanghae.lemonairservice.service;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.entity.MemberChannel;
import com.hanghae.lemonairservice.entity.Point;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberChannelRepository;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.PointRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@InjectMocks
	MemberService memberService;

	@Mock
	private PasswordEncoder passwordEncoder;


	@Mock
	MemberRepository memberRepository;

	@Mock
	MemberChannelRepository memberChannelRepository;

	@Mock
	PointRepository pointRepository;

	@Mock
	JwtUtil jwtUtil;

	@Mock
	RefreshTokenRepository refreshTokenRepository;


	@Test
	void SignUpSuccessTest(){
		SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder().email("kangminbeom@gmail.com").password("Kangminbeom1!")
			.password2("Kangminbeom1!").loginId("kangminbeom").nickname("kangminbeom").build();

		Member member = Member.builder().email("kangminbeom@gmail.com").password(passwordEncoder.encode("Kangminbeom1!"))
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();

		MemberChannel memberChannel = MemberChannel.builder().title("kangminbeom").onAir(false).totalStreaming(0).memberId(member.getId())
			.build();
		given(memberRepository.existsByEmail(signUpRequestDto.getEmail())).willReturn(Mono.just(false));
		given(memberRepository.existsByNickname(signUpRequestDto.getNickname())).willReturn(Mono.just(false));
		given(memberRepository.existsByLoginId(signUpRequestDto.getLoginId())).willReturn(Mono.just(false));
		given(memberRepository.save(Mockito.any(Member.class))).willReturn(Mono.just(member));

		given(memberChannelRepository.save(Mockito.any(MemberChannel.class))).willReturn(Mono.just(memberChannel));
		given(pointRepository.save(Mockito.any(Point.class))).willReturn(Mono.just(new Point(member)));

		StepVerifier.create(memberService.signup(signUpRequestDto))
			.expectNextMatches(signup ->{
				return signup.getStatusCode().is2xxSuccessful() && signup.getBody() != null;
			}).verifyComplete();
		//
		// verify(memberRepository.existsByEmail(signUpRequestDto.getEmail()));
		// verify(memberRepository.existsByNickname(signUpRequestDto.getNickname()));
		// verify(memberRepository.existsByLoginId(signUpRequestDto.getLoginId()));
		// verify(memberRepository.save(Mockito.any(Member.class)));
		// verify(memberChannelRepository.save(Mockito.any(MemberChannel.class)));
		// verify(pointRepository.save(Mockito.any(Point.class)));


			// .expectNextMatches(signup -> {
			//
			// 	return signup.getStatusCode() == signup.ok() && signup.getBody() != null;
			// })
	}

	@Test
	void SignUpFailTest(){
		SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder().email("kangminbeom@gmail.com").password("kangminbeom1!")
			.password2("kangminbeom1!").loginId("kangminbeom").nickname("kangminbeom").build();

		Member member = Member.builder().email("kangminbeom@gmail.com").password(passwordEncoder.encode("kangminbeom1!"))
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();

		MemberChannel memberChannel = MemberChannel.builder().title("kangminbeom").onAir(false).totalStreaming(0).memberId(member.getId())
			.build();

		given(memberRepository.existsByEmail(signUpRequestDto.getEmail())).willReturn(Mono.just(false));
		given(memberRepository.existsByNickname(signUpRequestDto.getNickname())).willReturn(Mono.just(false));
		given(memberRepository.existsByLoginId(signUpRequestDto.getLoginId())).willReturn(Mono.just(false));
		given(memberRepository.save(Mockito.any(Member.class))).willReturn(Mono.just(member));

		given(memberChannelRepository.save(Mockito.any(MemberChannel.class))).willReturn(Mono.just(memberChannel));
		given(pointRepository.save(Mockito.any(Point.class))).willReturn(Mono.just(new Point(member)));

		StepVerifier.create(memberService.signup(signUpRequestDto))
			.expectNextMatches(signup ->{
				return signup.getStatusCode().is2xxSuccessful() && signup.getBody() != null;
			}).verifyComplete();
	}

	@Test
	void LoginSuccessTest(){
		Member member = Member.builder().email("kangminbeom@gmail.com").password(passwordEncoder.encode("Kangminbeom1!"))
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();

		String accessToken = "1234";
		String refreshToken = "asdf";
		LoginRequestDto loginRequestDto = LoginRequestDto.builder().loginId("kangminbeom").password("Kangminbeom1!").build();
		given(memberRepository.findByLoginId(loginRequestDto.getLoginId())).willReturn(Mono.just(member));
		given(jwtUtil.createAccessToken(member.getLoginId(),member.getNickname())).willReturn(Mono.just(accessToken));
		given(jwtUtil.createRefreshToken(member.getLoginId(),member.getNickname())).willReturn(Mono.just(refreshToken));
		given(refreshTokenRepository.saveRefreshToken(member.getLoginId(),refreshToken)).willReturn(Mono.just(refreshToken));
		given(passwordEncoder.matches(loginRequestDto.getPassword(),member.getPassword())).willReturn(true);

		StepVerifier.create(memberService.login(loginRequestDto))
			.expectNextMatches(user -> {
				return user.getBody() != null && user.getBody().getAccessToken().equals(accessToken) &&
					user.getBody().getRefreshToken().equals(refreshToken);
			}).verifyComplete();
		//
		// verify(memberRepository.findByLoginId(loginRequestDto.getLoginId()));
		// verify(jwtUtil.createAccessToken(member.getLoginId(),member.getNickname()));
		// verify(refreshTokenRepository.saveRefreshToken(member.getLoginId(),accessToken));
		// verify(passwordEncoder.matches(loginRequestDto.getPassword(),member.getPassword()));
		// verify(jwtUtil.createRefreshToken(member.getLoginId(),member.getNickname()));
	}

	@Test
	void passwordErrorTest(){
		LoginRequestDto loginRequestDto = LoginRequestDto.builder().loginId("kangminbeom").password("Kangminbeom1!@").build();
		Member member1 = Member.builder().email("kangminbeom@gmail.com").password(passwordEncoder.encode("Kangminbeom1!"))
			.loginId("kangminbeom").nickname("kangminbeom").streamKey("1234").build();

		given(memberRepository.findByLoginId(loginRequestDto.getLoginId())).willReturn(Mono.just(member1));
		given(passwordEncoder.matches(loginRequestDto.getPassword(),"Kangminbeom1!")).willReturn(true);

		StepVerifier.create(memberService.login(loginRequestDto))
			.expectErrorMatches(throwable -> throwable instanceof NullPointerException &&
				throwable.getMessage().equals("비밀번호가 잘못되었습니다."));

		verify(memberRepository).findByLoginId(loginRequestDto.getLoginId());
		verify(passwordEncoder).matches(loginRequestDto.getPassword(),"Kangminbeom1!");
	}

	@Test
	void Logout(){
		String loginId = "kangminbeom";

		given(refreshTokenRepository.deleteByLoginId(loginId)).willReturn(Mono.empty());

		StepVerifier.create(memberService.logout(loginId))
			.verifyComplete();

		verify(refreshTokenRepository).deleteByLoginId(loginId);


	}

}
