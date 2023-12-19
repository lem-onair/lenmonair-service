package com.hanghae.lemonairservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.hanghae.lemonairservice.repository.RefreshTokenRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table("refreshtoken")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
	@Id
	private Long id;
	private String refreshToken;
	private String loginId;




}
