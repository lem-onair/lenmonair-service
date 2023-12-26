package com.hanghae.lemonairservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("follow")
@Getter
@Setter
public class Follow {
	@Id
	private Long id;
	@Column("streamer_id")
	private Long streamerId;
	@Column("member_id")
	private Long memberId;

	@Transient
	private MemberChannel memberChannel;

	public Follow(Long streamerId, Long memberId){
		this.streamerId = streamerId;
		this.memberId = memberId;
	}


}
