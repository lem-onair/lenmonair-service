package com.hanghae.lemonairservice.dto.point;

import lombok.Getter;

@Getter
public class DonationRankingDto {
	private String rank;

	public DonationRankingDto(String nickname) {
		this.rank = nickname;
	}
}

