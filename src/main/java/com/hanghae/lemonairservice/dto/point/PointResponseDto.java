package com.hanghae.lemonairservice.dto.point;

import com.hanghae.lemonairservice.entity.Member;

import lombok.Getter;

@Getter
public class PointResponseDto {
	private Long memberId;
	private int point;
	private String nickname;

	public PointResponseDto(Member member, int point) {
		this.memberId = member.getId();
		this.nickname = member.getNickname();
		this.point = point;
	}
}
