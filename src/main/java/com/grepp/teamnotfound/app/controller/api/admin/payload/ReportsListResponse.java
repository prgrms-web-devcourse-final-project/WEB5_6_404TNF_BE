package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.model.report.dto.ReportsListDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class ReportsListResponse {

    private List<ReportsListDto> reports;
    private PageInfo pageInfo;

    @Builder
    private ReportsListResponse(List<ReportsListDto> reports, PageInfo pageInfo) {
        this.reports = reports;
        this.pageInfo = pageInfo;
    }

    public static ReportsListResponse of(Page<ReportsListDto> reportPage) {
        return ReportsListResponse.builder()
                .reports(reportPage.getContent())
                .pageInfo(PageInfo.fromPage(reportPage))
                .build();
    }
}
