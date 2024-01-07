package com.hanghae.lemonairservice.dto.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class RefreshRequestDto {
	private String refreshToken;

	public RefreshRequestDto(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}