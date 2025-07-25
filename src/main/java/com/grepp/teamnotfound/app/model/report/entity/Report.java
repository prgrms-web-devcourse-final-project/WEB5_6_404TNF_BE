package com.grepp.teamnotfound.app.model.report.entity;

import com.grepp.teamnotfound.app.model.report.code.ReportCategory;
import com.grepp.teamnotfound.app.model.report.code.ReportState;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.dto.ReportCommand;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.infra.entity.BaseEntity;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.ReportErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

import lombok.*;


@Entity
@Table(name = "Reports")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
        name = "primary_sequence",
        sequenceName = "primary_sequence",
        allocationSize = 1,
        initialValue = 10000
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "primary_sequence"
    )
    private Long reportId;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ReportCategory category;

    @Column(nullable = false, columnDefinition = "text")
    private String reason;

    @Column
    private OffsetDateTime reportedAt;  // 처리일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportState state;

    @Column(columnDefinition = "text")
    private String adminReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;


    public static Report of(ReportCommand command, User reporter, User reported) {
        return Report.builder()
                .type(command.getReportType())
                .contentId(command.getContentId())
                .category(command.getReportCategory())
                .reason(command.getReason())
                .state(ReportState.PENDING)
                .reporter(reporter)
                .reported(reported)
                .build();
    }

    public void reject(String adminReason) {
        if (this.state != ReportState.PENDING) {
            throw new BusinessException(ReportErrorCode.ALREADY_COMPLETE_REPORT);
        }

        this.state = ReportState.REJECT;
        this.adminReason = adminReason;
        super.updatedAt = OffsetDateTime.now();
    }

    public void accept(String adminReason) {
        if (this.state != ReportState.PENDING) {
            throw new BusinessException(ReportErrorCode.ALREADY_COMPLETE_REPORT);
        }

        this.state = ReportState.ACCEPT;
        this.adminReason = adminReason;
        this.reportedAt = OffsetDateTime.now();
        super.updatedAt = OffsetDateTime.now();
    }
}

