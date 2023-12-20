package com.hanghae.lemonairservice.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("donation")
@Getter
@Setter
public class Donation {
	@Id
	private Long id;
	private Long streamerId;
	private Long donaterId;
	private String contents;
	private LocalDateTime donated_At;
	private int donatePoint;

	@Transient
	private Point point;

}
