package com.hanghae.lemonairservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table("point")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {
	@Id
	private Long id;
	@Column("member_id")
	private Long memberId;
	private int point;

	@Transient
	private Member member;

	public Point(Member member) {
		this.memberId = member.getId();
		this.point = 0;
	}
}
