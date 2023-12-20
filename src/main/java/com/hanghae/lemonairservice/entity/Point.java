package com.hanghae.lemonairservice.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("point")
@Getter
@Setter
public class Point {
	@Id
	private Long id;
	private Long memberId;
	private String nickname;
	private int point;

	@Transient
	private Member member;

	@Transient
	private List<PointLog> orders;

	public Point(Member member){
		this.memberId = member.getId();
		this.nickname = member.getNickname();
		this.point = 0;
	}

	public Point addPoint(int point) {
		this.point += point;
		return this;
	}

	public Point usePoint(int point) {
		this.point -= point;
		return this;
	}

}
