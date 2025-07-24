package com.grepp.teamnotfound.app.model.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class YearlyArticlesStatsDto implements StatsDto{

    private int year;
    private int articlesCount;

    public static YearlyArticlesStatsDto of(int year, int articlesCount){
        return YearlyArticlesStatsDto.builder()
                .year(year)
                .articlesCount(articlesCount)
                .build();
    }
}
