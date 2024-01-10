package com.hanghae.lemonairservice.dto.point;

import com.hanghae.lemonairservice.entity.Member;

import lombok.Getter;

@Getter
public class PointResponseDto {
	private Long memberId;
	private int point;

	public PointResponseDto(long memberId, int point) {
		this.memberId = memberId;
		this.point = point;
	}
}
