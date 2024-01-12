package com.hanghae.lemonairservice.dto.stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamKeyRequestDto {
	String streamKey;
}
