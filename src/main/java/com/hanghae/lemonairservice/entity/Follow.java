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
	private Long following;
	private Long follower;

	@Transient
	private MemberChannel memberChannel;

	public Follow(Long following, Long follower){
		this.following = following;
		this.follower = follower;
	}


}