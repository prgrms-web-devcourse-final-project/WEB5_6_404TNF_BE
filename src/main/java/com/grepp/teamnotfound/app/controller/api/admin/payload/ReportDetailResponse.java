package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.model.report.dto.ReportDetailDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportDetailResponse {

    private Long reportId;
    private String type;        // board or reply
    private Long contentId;     // articleid or replyid
    private Long articleId;     // articleid
    private String boardType;   // free, qna
    private String category;    // "ABUSE\" (or \"SPAM\", \"FRAUD\", \"ADULT_CONTENT\""
    private String reason;
    private String status;      // pending, reject, accept
    private String reporterNickname;
    private String reportedNickname;
    private String adminReason;
    private String reportedState;   // active, suspended, leave

    public static ReportDetailResponse from(ReportDetailDto dto) {
        return ReportDetailResponse.builder()
                .reportId(dto.getReportId())
                .type(dto.getType().name())
                .contentId(dto.getContentId())
                .articleId(dto.getArticleId())
                .boardType(dto.getBoardType())
                .category(dto.getCategory().name())
                .reason(dto.getReason())
                .status(dto.getStatus().name())
                .reporterNickname(dto.getReporterNickname())
                .reportedNickname(dto.getReportedNickname())
                .adminReason(dto.getAdminReason())
                .reportedState(dto.getReportedState().name())
                .build();
    }
}
