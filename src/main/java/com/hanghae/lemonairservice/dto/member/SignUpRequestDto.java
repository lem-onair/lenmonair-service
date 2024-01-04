package com.hanghae.lemonairservice.dto.member;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDto {

	@Email
	private String email;

	private String password;

	private String password2;

	private String loginId;

	private String nickname;
}

