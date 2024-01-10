package com.hanghae.lemonairservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.hanghae.lemonairservice.dto.point.DonationRequestDto;

import lombok.Getter;
import lombok.Setter;

@Table("point_log")
@Getter
@Setter
public class PointLog {
	@Id
	private Long id;
	@Column("streamer_id")
	private Long streamerId;
	@Column("donater_id")
	private Long donaterId;
	@Column("contents")
	private String contents;
	@Column("donated_at")
	private LocalDateTime donated_At;
	@Column("donate_point")
	private int donatePoint;


	public PointLog(Member member, DonationRequestDto donationRequestDto, Long streamerId) {
		this.streamerId = streamerId;
		this.donaterId = member.getId();
		this.contents = donationRequestDto.getContents();
		this.donatePoint = donationRequestDto.getDonatePoint();
	}
}
