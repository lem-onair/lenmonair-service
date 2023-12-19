package com.hanghae.lemonairservice.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {


    private final RefreshTokenRepository refreshTokenRepository;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secretKey}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public Mono<String> createToken(String loginId) {
        Date date = new Date();

        long TOKEN_TIME = 900 * 1000L;
        String token = BEARER_PREFIX +
            Jwts.builder()
                .setSubject(loginId)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

        return Mono.just(token);
    }

   
   public Mono<String> createRefreshToken(String loginId) {
        Date date = new Date();

        long TOKEN_TIME = 360 * 60 * 1000L;
        String token = BEARER_PREFIX +
            Jwts.builder()
                .setSubject(loginId)
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();

        return Mono.just(token);
    }

	// 토큰 생성
	public String createToken(String loginId) {
		Date date = new Date();


		long TOKEN_TIME = 360 * 60 * 1000L;
		return BEARER_PREFIX + Jwts.builder()
			.setSubject(loginId)
			.setExpiration(new Date(date.getTime() + TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		throw new NullPointerException("Not Found Token");
	}

	public Mono<Boolean> validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return Mono.just(true);
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return Mono.just(false);
	}

    public Mono<Boolean> validateRefreshToken(String token) {
        try {
            return Mono.just(Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody())
                .flatMap(tokenBody -> {
                    if (tokenBody.getSubject().equals("refreshToken")) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }


}
