package com.hanghae.lemonairservice.dto.point;

import lombok.Getter;

@Getter
public class DonationRankingDto {
	private String nickname;

	public DonationRankingDto(String nickname) {
		this.nickname = nickname;
	}
}

