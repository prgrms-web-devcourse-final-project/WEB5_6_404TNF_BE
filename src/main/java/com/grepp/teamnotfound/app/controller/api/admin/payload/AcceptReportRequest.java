package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.model.user.code.SuspensionPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class AcceptReportRequest {

    private Long reportId;
    @Schema(description = "제재 기간",
            example = "ONE_DAY | TWO_DAYS | THREE_DAYS | FIVE_DAYS | SEVEN_DAYS | FOURTEEN_DAYS | THIRTY_DAYS | THREE_MONTHS | ONE_YEAR | PERMANENT")
    private SuspensionPeriod period;
    private String adminReason;
}
