package com.hanghae.lemonairservice.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	public static final String BEARER_PREFIX = "Bearer ";
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	@Value("${jwt.secretKey}")
	private String secretKey;
	private Key key;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	// 토큰 생성
	public Mono<String> createAccessToken(String loginId, String nickname) {
		Date date = new Date();

		long TOKEN_TIME = 20 * 60 * 1000L;

		String token = BEARER_PREFIX + Jwts.builder()
			.claim("id", loginId)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();

		return Mono.just(token);
	}

	public Mono<String> createRefreshToken(String loginId, String nickname) {
		Date date = new Date();

		long TOKEN_TIME = 360 * 60 * 1000L;
		String token = BEARER_PREFIX + Jwts.builder()
			.claim("type", "refreshToken")
			.claim("id", loginId)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();

		return Mono.just(token);
	}

	public Mono<String> createChatToken(String loginId, String nickname) {
		Date date = new Date();

		long TOKEN_TIME = 30 * 1000L;
		String token = BEARER_PREFIX + Jwts.builder()
			.claim("type", "chatToken")
			.claim("id", loginId)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + TOKEN_TIME))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();

		return Mono.just(token);
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
			return Mono.just(false);
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return Mono.just(false);
	}

	public Mono<Boolean> validateRefreshToken(String token) {
		try {
			token = token.substring("Bearer ".length());
			return Mono.just(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody())
				.flatMap(tokenBody -> {
					if (tokenBody.get("type", String.class).equals("refreshToken")) {
						return Mono.just(true);
					} else {
						return Mono.just(false);
					}
				});
		} catch (Exception e) {
			return Mono.just(false);
		}
	}

	public JwtTokenSubjectDto getSubjectFromToken(String token) {
		try {
			if (token.startsWith("Bearer ")) {
				token = token.substring("Bearer ".length());
			}
			var jwtBody = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			log.info(jwtBody.get("id", String.class));
			log.info(jwtBody.get("nickname", String.class));
			return new JwtTokenSubjectDto(jwtBody.get("id", String.class), jwtBody.get("nickname", String.class));
		} catch (ExpiredJwtException e) {
			log.info("만료된 jwt 토큰일 경우에만 따로 처리하기");
			JsonNode expiredJwtBody = extractLoginIdFromExpiredJwtToken(token);

			return new JwtTokenSubjectDto(expiredJwtBody.get("id").asText(), expiredJwtBody.get("nickname").asText());
		}
	}

	private JsonNode extractLoginIdFromExpiredJwtToken(String jwt) {

		jwt = jwt.substring(jwt.indexOf('.') + 1, jwt.lastIndexOf('.'));
		log.info("만료된 jwt토큰의  payload 부분" + jwt);
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String claimsString = new String(decoder.decode(jwt));
		log.info("claimsString : " + claimsString);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(claimsString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("유효기간이 지난 Jwt토큰에서 로그인 ID 추출하는 Json Processing중 예외 발생");
		}
	}
}
