package com.hanghae.lemonairservice.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("member")
@Getter
@Setter
public class Member {

	@Id
	private Long id;

	private String email;

	private String loginId;

	private String password;

	private String nickname;

	private String streamKey;
	public Member(String email, String password, String loginId, String nickname) {
		this.email = email;
		this.password = password;
		this.loginId = loginId;
		this.nickname = nickname;
		this.streamKey = UUID.randomUUID().toString();
	}
}
