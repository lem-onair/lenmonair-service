package com.hanghae.lemonairservice.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpResponseDto {
	private String streamKey;

	public SignUpResponseDto(String streamKey) {
		this.streamKey = streamKey;
	}
}
