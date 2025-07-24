package com.grepp.teamnotfound.app.model.user.dto;

import com.grepp.teamnotfound.app.controller.api.admin.payload.AcceptReportRequest;
import com.grepp.teamnotfound.app.model.user.code.SuspensionPeriod;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcceptReportDto {

    private Long reportId;
    private SuspensionPeriod period;
    private String adminReason;

    public static AcceptReportDto from(AcceptReportRequest request) {
        return AcceptReportDto.builder()
                .reportId(request.getReportId())
                .adminReason(request.getAdminReason())
                .period(request.getPeriod())
                .build();
    }

}
