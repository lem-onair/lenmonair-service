package com.hanghae.lemonairservice.dto.follow;

import lombok.Getter;

@Getter
public class FollowResponseDto {
	private Long streamerId;
	private Long memberId;

	public FollowResponseDto(Long streamerId, Long memberId) {
		this.streamerId = streamerId;
		this.memberId = memberId;
	}
}
