package com.grepp.teamnotfound.app.model.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YearlyUserStatsDto implements StatsDto{

    private int year;
    private int joinedCount;
    private int leaveCount;

    public static YearlyUserStatsDto of(int year, int joinedCount, int leaveCount) {
        return YearlyUserStatsDto.builder()
                .year(year)
                .joinedCount(joinedCount)
                .leaveCount(leaveCount)
                .build();
    }
}
