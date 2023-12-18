package com.hanghae.lemonairservice.service;


import com.hanghae.lemonairservice.dto.member.LoginRequestDto;
import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberRepository;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberChannelService memberChannelService;
    private final JwtUtil jwtUtil;

    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public Mono<ResponseEntity<String>> signup(SignUpRequestDto signupRequestDto) {

        Mono<Boolean> emailExists = memberRepository.existsByEmail(signupRequestDto.getEmail());

        Mono<Boolean> nicknameExists = memberRepository.existsByNickname(
            signupRequestDto.getNickname());

        if (!validatePassword(signupRequestDto.getPassword())) {
            return Mono.just(
                ResponseEntity.badRequest().body("비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."));
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPassword2())) {
            return Mono.just(ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다."));
        }
		
        return emailExists.zipWith(nicknameExists)
            .flatMap(tuple -> {
                boolean emailExistsValue = tuple.getT1();
                boolean nicknameExistsValue = tuple.getT2();
                if (emailExistsValue) {
                    return Mono.just(ResponseEntity.badRequest().body("해당 이메일은 이미 사용 중입니다."));
                } else if (nicknameExistsValue) {
                    return Mono.just(ResponseEntity.badRequest().body("해당 닉네임은 이미 사용 중입니다."));
                } else {
                    Member newMember = new Member(
                        signupRequestDto.getEmail(),
                        passwordEncoder.encode(signupRequestDto.getPassword()),
                        signupRequestDto.getName(),
                        signupRequestDto.getNickname()
                    );

                    return memberRepository.save(newMember)
                        .flatMap(savedMember -> memberChannelService.createChannel(savedMember.getNickname()))
                        .log()
                        .map(savedMember -> ResponseEntity.ok().body("회원가입이 완료되었습니다."))
                        .onErrorResume(throwable -> {
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("회원가입에 실패했습니다."));
                        });
                }
            });
    }

    private static boolean validatePassword(String password) {
        return pattern.matcher(password).matches();
    }

    public Mono<ResponseEntity<String>> login(LoginRequestDto loginRequestDto) {
        return memberRepository.findByEmail(loginRequestDto.getEmail())
            .flatMap(member -> {
                if (passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
                    String token = jwtUtil.createToken(member.getLoginId());
                    return Mono.just(ResponseEntity.ok().body(token));
                } else {
                    return Mono.just(ResponseEntity.badRequest().body("이메일 또는 비밀번호가 잘못되었습니다."));
                }
            })
            .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("이메일 또는 비밀번호가 잘못되었습니다.")));
    }
}


