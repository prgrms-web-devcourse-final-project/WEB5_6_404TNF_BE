package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.controller.api.admin.code.AdminListSortDirection;
import com.grepp.teamnotfound.app.controller.api.admin.code.ReportStateFilter;
import com.grepp.teamnotfound.app.controller.api.admin.code.ReportsListSortBy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReportsListRequest {

    @Min(value = 1, message="페이지는 1이상이어야 합니다.")
    private int page=1;
    @Min(value = 1, message="사이즈는 1이상이어야 합니다.")
    private int size=5;

    private String search;

    @Schema(description = "정렬 방법", example = "ASC | DESC", defaultValue = "ASC")
    private AdminListSortDirection sort = AdminListSortDirection.ASC;
    @Schema(description = "신고 목록 정렬 기준",
            example = "REPORTED_AT | REPORTER_NICKNAME | REPORTED_NICKNAME | CONTENT_TYPE | REASON | STATUS",
            defaultValue = "REPORTED_AT")
    private ReportsListSortBy sortBy = ReportsListSortBy.REPORTED_AT;
    @Schema(description = "신고 상태 필터", example = "ALL | PENDING | ACCEPT | REJECT", defaultValue = "ALL")
    private ReportStateFilter status = ReportStateFilter.ALL;
}
