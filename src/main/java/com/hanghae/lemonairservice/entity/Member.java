package com.hanghae.lemonairservice.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.hanghae.lemonairservice.dto.member.SignUpRequestDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("member")
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	@Id
	private Long id;

	private String email;

	private String loginId;

	private String password;

	private String nickname;

	private String streamKey;

	public Member(SignUpRequestDto signUpRequestDto, String encryptedPassword) {
		this.email = signUpRequestDto.getEmail();
		this.password = encryptedPassword;
		this.loginId = signUpRequestDto.getLoginId();
		this.nickname = signUpRequestDto.getNickname();
		this.streamKey = UUID.randomUUID().toString();
	}
}
