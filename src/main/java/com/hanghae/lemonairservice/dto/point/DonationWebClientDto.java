package com.hanghae.lemonairservice.dto.point;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonationWebClientDto {

    private String nickname;
    private Long streamerId;
    private String contents;
    private int donatePoint;

    public DonationWebClientDto(String nickname, Long streamerId, String contents,
        int donatePoint) {
        this.nickname = nickname;
        this.streamerId = streamerId;
        this.contents = contents;
        this.donatePoint = donatePoint;
    }
}
