package com.hanghae.lemonairservice.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table("member_channel")
public class MemberChannel {
	@Id
	private Long id;
	private String title;
	private Boolean onAir;
	private LocalDateTime startedAt;

	@Column("total_streaming")
	private int totalStreaming;

	@Column("member_id")
	private long memberId;

	@Setter
	@Transient
	private Member member;

	public MemberChannel(Member member) {
		this.title = member.getNickname() + "의 방송";
		this.onAir = false;
		this.totalStreaming = 0;
		this.memberId = member.getId();
	}

	public void onAir() {
		this.onAir = true;
		this.startedAt = LocalDateTime.now();
	}

	public void offAir() {
		this.onAir = false;
		if (this.startedAt == null) {
			return;
		}
		this.totalStreaming += (int)Duration.between(this.startedAt, LocalDateTime.now()).toMinutes();
	}
}
