package com.hanghae.lemonairservice.dto.token;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshResponseDto {
	private String refreshToken;

	public RefreshResponseDto(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}