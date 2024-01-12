package com.hanghae.lemonairservice.dto.point;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DonationRequestDto {
	private int donatePoint;
	private String contents;

}