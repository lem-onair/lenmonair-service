package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberChannelDetailResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String hlsUrl;
	public MemberChannelDetailResponseDto(MemberChannel memberChannel, String hlsUrl) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getMember().getNickname();
		this.title = memberChannel.getTitle();
		this.hlsUrl = hlsUrl;
	}
}
