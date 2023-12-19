package com.hanghae.lemonairservice.dto.token;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshRequestDto {
	private String refreshToken;

	public RefreshRequestDto(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}