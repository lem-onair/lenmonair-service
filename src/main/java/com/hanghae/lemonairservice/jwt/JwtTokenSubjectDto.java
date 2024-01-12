package com.hanghae.lemonairservice.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenSubjectDto {
	private String loginId;
	private String nickname;

	public JwtTokenSubjectDto(String loginId, String nickname) {
		this.loginId = loginId;
		this.nickname = nickname;
	}
}
