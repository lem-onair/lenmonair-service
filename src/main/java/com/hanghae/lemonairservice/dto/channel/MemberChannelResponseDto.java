package com.hanghae.lemonairservice.dto.channel;

import com.hanghae.lemonairservice.entity.MemberChannel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder // 8-1. Builder 어노테이션을 적용하여 귀찮은 빌더함수 생성을 Lombok에게 맡깁니다.
@AllArgsConstructor(access = AccessLevel.PROTECTED) // 8-2. builder 어노테이션을 사용하는 경우 모든 필드를 매개변수로 하는 생성자가 있어야합니다.
// 8-3. AccessLevel.PROTECTED로지정하면 개발자는 직접 생성자에 접근하지 못하지만, Builder는 접근할 수 있습니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 13-1. test code 리턴 형식 검증에 사용됩니다.
public class MemberChannelResponseDto {
	private Long channelId;
	private String streamerNickname;
	private String title;
	private String thumbnailUrl;

	public MemberChannelResponseDto(MemberChannel memberChannel, String thumbnailUrl) {
		this.channelId = memberChannel.getId();
		this.streamerNickname = memberChannel.getMember().getNickname();
		this.title = memberChannel.getTitle();
		this.thumbnailUrl = thumbnailUrl;
	}
}
