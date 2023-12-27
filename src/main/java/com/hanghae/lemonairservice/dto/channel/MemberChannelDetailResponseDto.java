package com.hanghae.lemonairservice.dto.channel;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
	private String chatToken;
	private String chattingRoomId;
	public MemberChannelDetailResponseDto(MemberChannel memberChannel, String hlsUrl, String chatToken) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getMember().getNickname();
		this.title = memberChannel.getTitle();
		this.hlsUrl = hlsUrl;
		this.chatToken = chatToken;
		// this.chattingRoomId = Base64.getEncoder().encodeToString(this.streamerNickname.getBytes(StandardCharsets.UTF_8));
		this.chattingRoomId = this.streamerNickname;
	}
}
