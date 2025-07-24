package com.grepp.teamnotfound.app.model.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class TotalUsersDto {

    private OffsetDateTime date;
    private long total;

    public static TotalUsersDto of(long totalUsers) {
        return TotalUsersDto.builder()
                .date(OffsetDateTime.now())
                .total(totalUsers)
                .build();
    }
}
