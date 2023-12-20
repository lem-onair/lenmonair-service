package com.hanghae.lemonairservice.service;

import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.LoginResponseDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpResponseDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberRepository;
import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import java.security.Principal;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

  
   private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberChannelService memberChannelService;
    private final PointService pointService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    @Transactional
    public Mono<ResponseEntity<SignUpResponseDto>> signup(SignUpRequestDto signupRequestDto) {

        if (!validatePassword(signupRequestDto.getPassword())) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."
            ));
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPassword2())) {
            return Mono.error(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."
            ));
        }

        Mono<Boolean> emailExists = memberRepository.existsByEmail(signupRequestDto.getEmail());
        Mono<Boolean> nicknameExists = memberRepository.existsByNickname(signupRequestDto.getNickname());
        Mono<Boolean> useridExists = memberRepository.existsByLoginId(signupRequestDto.getLoginId());

        return emailExists.zipWith(nicknameExists.zipWith(useridExists))
            .flatMap(tuple -> {
                boolean emailExistsValue = tuple.getT1();
                Tuple2<Boolean, Boolean> nestedTuple = tuple.getT2();
                boolean nicknameExistsValue = nestedTuple.getT1();
                boolean useridExistsValue = nestedTuple.getT2();

                if (emailExistsValue) {
                    return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "해당 이메일은 이미 사용 중입니다."
                    ));
                } else if (useridExistsValue) {
                    return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "해당 아이디는 이미 사용 중입니다."
                    ));
                } else if (nicknameExistsValue) {
                    return Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "해당 닉네임은 이미 사용 중입니다."
                    ));
                } else {
                    String streamKey = UUID.randomUUID().toString();
                    Member newMember = new Member(
                        signupRequestDto.getEmail(),
                        passwordEncoder.encode(signupRequestDto.getPassword()),
                        signupRequestDto.getLoginId(),
                        signupRequestDto.getNickname(),
                        streamKey
                    );

                    return memberRepository.save(newMember)
                        .flatMap(memberChannelService::createChannel)
                        .flatMap(savedMember -> pointService.createPoint(savedMember.getMember()) // 여기에서 pointService::createPoint 호출
                            .thenReturn(savedMember)) //
                        .map(savedMember -> ResponseEntity.ok().body(new SignUpResponseDto(streamKey)))
                        .onErrorResume(throwable -> Mono.error(new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR, "회원가입에 실패했습니다."
                        )));
                }
            });
    }

    private static boolean validatePassword(String password) {
        return pattern.matcher(password).matches();
    }


    public Mono<ResponseEntity<LoginResponseDto>> login(LoginRequestDto loginRequestDto) {
        return memberRepository.findByLoginId(loginRequestDto.getLoginId()).flatMap(member -> {
            if (passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
                return jwtUtil.createToken(member.getLoginId())
                    .flatMap(accessToken -> jwtUtil.createRefreshToken(member.getLoginId())
                        .flatMap(
                            refreshToken -> refreshTokenRepository.saveRefreshToken(member.getLoginId(), refreshToken)
                                .thenReturn(
                                    ResponseEntity.ok().body(new LoginResponseDto(accessToken, refreshToken)))));

            } else {
                return Mono.error(new RuntimeException("비밀번호가 잘못되었습니다."));
            }
        }).switchIfEmpty(Mono.error(new RuntimeException("아이디가 잘못되었습니다.")));
    }

    public Mono<ResponseEntity<String>> logout(String loginId){
        return refreshTokenRepository.deleteByLoginId(loginId)
            .flatMap(logout -> Mono.just(ResponseEntity.ok("로그아웃되었습니다.")));
    }

}


