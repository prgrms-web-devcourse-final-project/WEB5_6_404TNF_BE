package com.grepp.teamnotfound.app.model.report.dto;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.report.code.ReportCategory;
import com.grepp.teamnotfound.app.model.report.code.ReportState;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.user.code.UserStateResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportDetailDto {

    private Long reportId;
    private ReportType type;        // board or reply
    private Long contentId;     // articleId or replyId
    private Long articleId;     // articleId or type이 reply일 때 해당 reply의 articleId
    private ReportCategory category;    // "ABUSE" or "SPAM", "FRAUD", "ADULT_CONTENT"
    private String reason;
    private ReportState status;      // pending, accept, reject
    private String boardType;
    private String reporterNickname;
    private String reportedNickname;
    private String adminReason;
    private UserStateResponse reportedState;


    public static ReportDetailDto from(Report report, Article article) {
        return ReportDetailDto.builder()
                .reportId(report.getReportId())
                .type(report.getType())
                .contentId(report.getContentId())
                .articleId(article.getArticleId())
                .category(report.getCategory())
                .reason(report.getReason())
                .status(report.getState())
                .boardType(article.getBoard().getName())
                .reporterNickname(report.getReporter().getNickname())
                .reportedNickname(report.getReported().getNickname())
                .adminReason(report.getAdminReason())
                .reportedState(report.getReported().getUserState())
                .build();
    }
}
