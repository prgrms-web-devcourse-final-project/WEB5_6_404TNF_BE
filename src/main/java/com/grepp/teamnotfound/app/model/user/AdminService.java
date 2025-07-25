package com.grepp.teamnotfound.app.model.user;

import com.grepp.teamnotfound.app.controller.api.admin.payload.ReportsListRequest;
import com.grepp.teamnotfound.app.controller.api.admin.payload.UsersListRequest;
import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiServiceCreateDto;
import com.grepp.teamnotfound.app.model.notification.handler.NotiAppender;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.app.model.report.code.ReportState;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.dto.ReportsListDto;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.report.repository.ReportRepository;
import com.grepp.teamnotfound.app.model.user.dto.*;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReplyErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReportErrorCode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ArticleRepository articleRepository;
    private final ReplyRepository replyRepository;
    private final NotiAppender notiAppender;

    @Transactional(readOnly = true)
    public TotalUsersDto getTotalUsersCount() {
        long totalUsers = userRepository.count();
        return TotalUsersDto.of(totalUsers);
    }

    @Transactional(readOnly = true)
    public List<MonthlyUserStatsDto> getMonthlyUsersStats() {
        OffsetDateTime now = OffsetDateTime.now();

        List<MonthlyUserStatsDto> response = new ArrayList<>();

        // 옛날부터
        for(int i = 4; i>=0; i--){
            OffsetDateTime monthStart = now.minusMonths(i).withDayOfMonth(1);
            OffsetDateTime monthEnd = monthStart.withDayOfMonth(monthStart.toLocalDate().lengthOfMonth());

            int joined = userRepository.countJoinedUsersBetween(monthStart, monthEnd);
            int left = userRepository.countLeftUsersBetween(monthStart, monthEnd);

            MonthlyUserStatsDto stats = MonthlyUserStatsDto.of(monthStart.getMonthValue(), joined, left);
            response.add(stats);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<YearlyUserStatsDto> getYearlyUsersStats() {
        OffsetDateTime now = OffsetDateTime.now();

        List<YearlyUserStatsDto> response = new ArrayList<>();

        for(int i = 4; i>=0; i--){
            OffsetDateTime yearStart = now.minusYears(i).withDayOfYear(1);
            OffsetDateTime yearEnd = yearStart.withDayOfYear(yearStart.toLocalDate().lengthOfYear());

            int joined = userRepository.countJoinedUsersBetween(yearStart, yearEnd);
            int left = userRepository.countLeftUsersBetween(yearStart, yearEnd);

            YearlyUserStatsDto stats = YearlyUserStatsDto.of(yearStart.getYear(), joined, left);
            response.add(stats);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<MonthlyArticlesStatsDto> getMonthlyArticlesStats() {

        OffsetDateTime now = OffsetDateTime.now();

        List<MonthlyArticlesStatsDto> response = new ArrayList<>();

        for(int i = 4; i>=0; i--){
            OffsetDateTime monthStart = now.minusMonths(i).withDayOfMonth(1);
            OffsetDateTime monthEnd = monthStart.withDayOfMonth(monthStart.toLocalDate().lengthOfMonth());

            int articles = articleRepository.countArticlesBetween(monthStart, monthEnd);

            MonthlyArticlesStatsDto stats = MonthlyArticlesStatsDto.of(monthStart.getMonthValue(), articles);
            response.add(stats);
        }

        return response;

    }

    @Transactional(readOnly = true)
    public List<YearlyArticlesStatsDto> getYearlyArticlesStats() {
        OffsetDateTime now = OffsetDateTime.now();

        List<YearlyArticlesStatsDto> response = new ArrayList<>();

        for(int i = 4; i>=0; i--){
            OffsetDateTime yearStart = now.minusYears(i).withDayOfYear(1);
            OffsetDateTime yearEnd = yearStart.withDayOfYear(yearStart.toLocalDate().lengthOfYear());

            int articles = articleRepository.countArticlesBetween(yearStart, yearEnd);

            YearlyArticlesStatsDto stats = YearlyArticlesStatsDto.of(yearStart.getYear(), articles);
            response.add(stats);
        }

        return response;

    }

    @Transactional
    public void rejectReport(RejectReportDto dto) {
        Report targetReport = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));

        targetReport.reject(dto.getAdminReason());

        NotiServiceCreateDto notiDto1 = NotiServiceCreateDto.builder()
            .targetId(targetReport.getReportId())
            .build();
        notiAppender.append(targetReport.getReporter().getUserId(), NotiType.REPORT_FAIL, notiDto1);

        // 같은 contentId, 같은 category, PENDING인 report에 대해 reject 처리
        // 방법 1. report repo에서 List<Report> reports 를 가져옴
        //        for 를 돌면서 report.reject(dto.getAdminReason());
        List<Report> reports = reportRepository.findByContentIdAndReportCategoryAndReportTypeState(
                targetReport.getContentId(),
                targetReport.getCategory(),
                targetReport.getType(),
                ReportState.PENDING
        );
        for (Report report : reports) {
            report.reject(dto.getAdminReason());

            NotiServiceCreateDto notiDtos = NotiServiceCreateDto.builder()
                .targetId(report.getReportId())
                .build();
            notiAppender.append(report.getReporter().getUserId(), NotiType.REPORT_FAIL, notiDtos);
        }

        // 방법 2. 벌크 연산 - updatedAt 추가 필요
//        reportRepository.bulkRejectPendingReports(
//                targetReport.getContentId(),
//                targetReport.getCategory(),
//                ReportState.REJECT,
//                dto.getAdminReason(),
//                ReportState.PENDING
//        );
    }

    @Transactional
    public void acceptReportAndSuspendUser(AcceptReportDto dto) {
        Report targetReport = reportRepository.findWithReportedUserById(dto.getReportId())
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));

        insertReportedAtOfContent(targetReport);
        targetReport.accept(dto.getAdminReason());

        NotiServiceCreateDto notiDto1 = NotiServiceCreateDto.builder()
            .targetId(targetReport.getReportId())
            .build();
        notiAppender.append(targetReport.getReporter().getUserId(), NotiType.REPORT_SUCCESS, notiDto1);
        notiAppender.append(targetReport.getReported().getUserId(), NotiType.REPORTED, notiDto1);

        List<Report> reports = reportRepository.findByContentIdAndReportCategoryAndReportTypeState(
                targetReport.getContentId(),
                targetReport.getCategory(),
                targetReport.getType(),
                ReportState.PENDING
        );
        for (Report report : reports) {
            report.accept(dto.getAdminReason());

            NotiServiceCreateDto notiDtos = NotiServiceCreateDto.builder()
                .targetId(report.getReportId())
                .build();
            notiAppender.append(report.getReporter().getUserId(), NotiType.REPORT_SUCCESS, notiDtos);
            notiAppender.append(report.getReported().getUserId(), NotiType.REPORTED, notiDtos);
        }

        User user = targetReport.getReported();
        user.suspend(dto.getPeriod());
    }

    private void insertReportedAtOfContent(Report targetReport) {
        targetReport.getType().processReport(
                targetReport.getContentId(),
                articleRepository,
                replyRepository
        );
    }

    public Page<UsersListDto> getUsersList(UsersListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() -1, request.getSize());
        return userRepository.findUserListWithMeta(request, pageable);
    }

    public Page<ReportsListDto> getReportsList(ReportsListRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() -1, request.getSize());
        return reportRepository.findReportListWithMeta(request, pageable);
    }
}
