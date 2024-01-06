package com.hanghae.lemonairservice.dto.point;

import java.util.List;

import com.hanghae.lemonairservice.entity.PointLog;

import lombok.Getter;

@Getter
public class DonationRankingDto {
	private String nickname;

	public DonationRankingDto(String nickname) {
		this.nickname = nickname;
	}
}
