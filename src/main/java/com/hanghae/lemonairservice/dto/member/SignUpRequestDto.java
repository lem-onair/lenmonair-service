package com.hanghae.lemonairservice.dto.member;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

	@Email
	private String email;

	private String password;

	private String password2;

	private String userid;

	private String name;

	private String nickname;
}

