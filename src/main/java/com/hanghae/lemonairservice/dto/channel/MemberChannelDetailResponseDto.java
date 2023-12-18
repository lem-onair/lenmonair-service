package com.hanghae.lemonairservice.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.Getter;

@Getter
public class MemberChannelDetailResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String hlsUrl;

	@JsonIgnore
	private String streamerLoginId;

	public MemberChannelDetailResponseDto(MemberChannel memberChannel){
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getStreamerNickname();
		this.title = memberChannel.getTitle();
		this.streamerLoginId = memberChannel.getMemberLoginId();
	}
	public void updateHlsUrl(String hlsUrl){
		this.hlsUrl= hlsUrl;
	}

}
