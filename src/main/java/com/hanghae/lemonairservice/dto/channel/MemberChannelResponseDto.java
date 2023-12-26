package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberChannelResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String thumbnailUrl;

	public MemberChannelResponseDto(MemberChannel memberChannel, String thumbnailUrl) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getStreamerNickname();
		this.title = memberChannel.getTitle();
		this.thumbnailUrl = thumbnailUrl;
	}
}
