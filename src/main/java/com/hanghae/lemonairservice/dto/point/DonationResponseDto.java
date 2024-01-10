package com.hanghae.lemonairservice.dto.point;

import com.hanghae.lemonairservice.entity.Member;

import lombok.Getter;

@Getter
public class DonationResponseDto {
	private Long donaterId;
	private Long streamerId;
	private String donaterNickname;
	private String content;
	private int remainingPoint;

	public DonationResponseDto(Long donatorId, Long streamerId, String contents, int remainingPoint) {
		this.donaterId = donatorId;
		this.streamerId = streamerId;
		this.content = contents;
		this.remainingPoint = remainingPoint;
	}
}
