package com.hanghae.lemonairservice.service;


import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;
import com.hanghae.lemonairservice.entity.Member;
import com.hanghae.lemonairservice.repository.MemberRepository;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberChannelService memberChannelService;

    private static final String PASSWORD_PATTERN =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public Mono<ResponseEntity<String>> signup(SignUpRequestDto signupRequestDto) {

        Mono<Boolean> emailExists = memberRepository.existsByEmail(signupRequestDto.getEmail());

        Mono<Boolean> nicknameExists = memberRepository.existsByNickname(
            signupRequestDto.getNickname());

        Mono<Boolean> useridExists = memberRepository.existsByUserid(signupRequestDto.getUserid());



        if (!validatePassword(signupRequestDto.getPassword())) {
            return Mono.just(
                ResponseEntity.badRequest().body("비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다."));
        }

        if (!signupRequestDto.getPassword().equals(signupRequestDto.getPassword2())) {
            return Mono.just(ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다."));
        }
		
        return emailExists.zipWith(nicknameExists.zipWith(useridExists))
            .flatMap(tuple -> {
                boolean emailExistsValue = tuple.getT1();
                Tuple2<Boolean, Boolean> nestedTuple = tuple.getT2();
                boolean nicknameExistsValue = nestedTuple.getT1();
                boolean useridExistsValue = nestedTuple.getT2();

                if (emailExistsValue) {
                    return Mono.just(ResponseEntity.badRequest().body("해당 이메일은 이미 사용 중입니다."));
                } else if (useridExistsValue) {
                    return Mono.just(ResponseEntity.badRequest().body("해당 아이디는 이미 사용 중입니다."));
                } else if (nicknameExistsValue) {
                    return Mono.just(ResponseEntity.badRequest().body("해당 닉네임은 이미 사용 중입니다."));
                } else {
                    Member newMember = new Member(
                        signupRequestDto.getEmail(),
                        passwordEncoder.encode(signupRequestDto.getPassword()),
                        signupRequestDto.getUserid(),
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
}


