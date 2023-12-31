package com.hanghae.lemonairservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.hanghae.lemonairservice.security.AuthenticationManager;
import com.hanghae.lemonairservice.security.SecurityContextRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
@RequiredArgsConstructor
public class WebfluxSecurityConfiguration {

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
			.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

			.authorizeExchange((exchanges) -> exchanges.pathMatchers(HttpMethod.OPTIONS).permitAll())
			.authorizeExchange(
				(exchanges) -> exchanges.pathMatchers("/api/channels/**", "/api/signup", "/api/logout", "/api/login",
					"/api/streams/**").permitAll().anyExchange().authenticated())

			.securityContextRepository(securityContextRepository)
			.authenticationManager(authenticationManager)
			.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
					(serverWebExchange, e) -> Mono.fromRunnable(
						() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
				.accessDeniedHandler((serverWebExchange, e) -> Mono.fromRunnable(
					() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))));
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
