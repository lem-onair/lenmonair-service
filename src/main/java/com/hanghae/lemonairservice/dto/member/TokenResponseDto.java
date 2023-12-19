package com.hanghae.lemonairservice.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponseDto {
    private String jwtToken;

    public TokenResponseDto(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
