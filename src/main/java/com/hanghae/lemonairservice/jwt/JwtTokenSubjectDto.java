package com.hanghae.lemonairservice.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class JwtTokenSubjectDto {
	private String loginId;
	private String nickname;

	public JwtTokenSubjectDto(String loginId, String nickname) {
		this.loginId = loginId;
		this.nickname = nickname;
	}
}
