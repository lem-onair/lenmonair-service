package com.hanghae.lemonairservice.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.Getter;

@Getter
public class MemberChannelResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String thumbnailUrl;

	@JsonIgnore
	private String streamerLoginId;

	public MemberChannelResponseDto(MemberChannel memberChannel) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getStreamerNickname();
		this.title = memberChannel.getTitle();
		this.streamerLoginId = memberChannel.getMemberLoginId();
	}

	public void updateThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
}
