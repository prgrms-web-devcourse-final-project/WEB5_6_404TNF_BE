package com.grepp.teamnotfound.app.model.user.dto;

import com.grepp.teamnotfound.app.controller.api.admin.payload.RejectReportRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RejectReportDto {

    private Long reportId;
    private String adminReason;

    public static RejectReportDto from(RejectReportRequest request) {
        return RejectReportDto.builder()
                .reportId(request.getReportId())
                .adminReason(request.getAdminReason())
                .build();
    }
}
