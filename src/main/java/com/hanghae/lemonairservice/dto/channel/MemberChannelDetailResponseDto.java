package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.Getter;

@Getter
public class MemberChannelDetailResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String hlsUrl;
	private String chattingRoomId;

	public MemberChannelDetailResponseDto(MemberChannel memberChannel, String hlsUrl) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getMember().getNickname();
		this.title = memberChannel.getTitle();
		this.hlsUrl = hlsUrl;
		// this.chattingRoomId = Base64.getEncoder().encodeToString(this.streamerNickname.getBytes(StandardCharsets.UTF_8));
		this.chattingRoomId = memberChannel.getMember().getLoginId();
	}
}
