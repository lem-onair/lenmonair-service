package com.hanghae.lemonairservice.dto.point;

import com.hanghae.lemonairservice.entity.Member;

import lombok.Getter;

@Getter
public class PointResponseDto {
	private Long memberId;
	private Long point;
	private String loginId;

	public PointResponseDto(Member member, Long point) {
		this.memberId = member.getId();
		this.loginId = member.getLoginId();
		this.point = point;
	}
}
