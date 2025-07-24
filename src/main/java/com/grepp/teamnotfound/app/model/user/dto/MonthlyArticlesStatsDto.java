package com.grepp.teamnotfound.app.model.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyArticlesStatsDto implements StatsDto{

    private int month;
    private int articlesCount;

    public static MonthlyArticlesStatsDto of(int month, int articlesCount) {
        return MonthlyArticlesStatsDto.builder()
                .month(month)
                .articlesCount(articlesCount)
                .build();
    }
}
