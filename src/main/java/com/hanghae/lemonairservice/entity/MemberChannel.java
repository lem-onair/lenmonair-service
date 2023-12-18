package com.hanghae.lemonairservice.entity;

import org.springframework.data.annotation.Id;
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
	private Long memberId;
	private String memberLoginId;
	private String title;
	private String streamerNickname;
	private Boolean onAir;

	public MemberChannel(Long memberId, String memberLoginId, String nickname){
		this.title = nickname + "의 방송";
		this.streamerNickname = nickname;
		this.memberLoginId = memberLoginId;
		this.memberId = memberId;
		this.onAir = false;
	}
}
