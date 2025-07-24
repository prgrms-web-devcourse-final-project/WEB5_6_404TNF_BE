package com.grepp.teamnotfound.app.model.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyUserStatsDto implements StatsDto{

    private int month;
    private int joinedCount;
    private int leaveCount;

    public static MonthlyUserStatsDto of(int month, int joinedCount, int leaveCount) {
        return MonthlyUserStatsDto.builder()
                .month(month)
                .joinedCount(joinedCount)
                .leaveCount(leaveCount)
                .build();
    }
}
