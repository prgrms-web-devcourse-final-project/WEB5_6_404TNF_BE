package com.grepp.teamnotfound.app.controller.api.admin.payload;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class ArticlesStatsResponse<T> {


    private OffsetDateTime viewDate;
    private List<T> stats;

}
