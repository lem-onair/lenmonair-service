package com.hanghae.lemonairservice.entity;

import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table("member_channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChannel {
	@Id
	private Long id;
	private String title;
	private String streamerNickname;
	private Boolean onAir;

	// @Column("total_streaming")
	// private Integer totalStreaming;

	@Column("member_id")
	private Long memberId;

	@Transient
	private Member member;

	public MemberChannel(Member member) {
		this.title = member.getNickname() + "의 방송";
		this.streamerNickname = member.getNickname();
		this.onAir = false;
		// this.totalStreaming = 0;
		this.memberId = member.getId();
	}

	// public MemberChannel addTime(int time){
	// 	this.totalStreaming += time;
	// 	return this;
	// }

}
