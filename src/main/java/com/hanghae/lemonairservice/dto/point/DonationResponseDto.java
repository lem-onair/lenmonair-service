package com.hanghae.lemonairservice.dto.point;

import lombok.Getter;

@Getter
public class DonationResponseDto {
	private Long donaterId;
	private Long streamerId;
	private String donaterNickname;
	private String content;
	private int remainingPoint;
	private String donated;

	public DonationResponseDto(Long donaterId, String nickname, Long streamerId, String contents, int remainingPoint,
		String now) {
		this.donaterId = donaterId;
		this.streamerId = streamerId;
		this.donaterNickname = nickname;
		this.content = contents;
		this.remainingPoint = remainingPoint;
		this.donated = now;
	}
}
