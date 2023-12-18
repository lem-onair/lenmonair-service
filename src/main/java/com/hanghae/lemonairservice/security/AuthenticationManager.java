package com.hanghae.lemonairservice.security;

import com.hanghae.lemonairservice.jwt.JwtUtil;
import com.hanghae.lemonairservice.repository.MemberRepository;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String loginId = jwtUtil.getUserInfoFromToken(authToken);

        return jwtUtil.validateToken(authToken)
            .filter(valid -> valid)
            .flatMap(valid -> userDetailsService.findByUsername(loginId))
            .map(user -> {
                Set<GrantedAuthority> emptyAuthorities = Collections.emptySet();
                return new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    emptyAuthorities
                );
            });
    }
}