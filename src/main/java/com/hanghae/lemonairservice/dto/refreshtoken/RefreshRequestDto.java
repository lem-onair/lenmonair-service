package com.hanghae.lemonairservice.dto.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RefreshRequestDto {
	private String refreshToken;

	public RefreshRequestDto(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}