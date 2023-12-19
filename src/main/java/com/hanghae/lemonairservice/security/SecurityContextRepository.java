package com.hanghae.lemonairservice.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import com.hanghae.lemonairservice.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	@Override
	public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext sc) {
		return Mono.empty();
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
		return Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
			.filter(authHeader -> authHeader.startsWith("Bearer "))
			.flatMap(authHeader -> {
				String authToken = jwtUtil.substringToken(authHeader);
				Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
				return this.authenticationManager.authenticate(auth)
					.map(SecurityContextImpl::new);
			});
	}
}