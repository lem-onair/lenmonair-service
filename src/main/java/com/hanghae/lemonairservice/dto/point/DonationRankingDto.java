package com.hanghae.lemonairservice.dto.point;

import java.util.List;

import com.hanghae.lemonairservice.entity.PointLog;

import lombok.Getter;

@Getter
public class DonationRankingDto {
	private String rank;

	public DonationRankingDto(String nickname) {
		this.rank = nickname;
	}
}
