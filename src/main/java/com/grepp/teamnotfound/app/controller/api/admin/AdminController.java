package com.grepp.teamnotfound.app.controller.api.admin;

import com.grepp.teamnotfound.app.controller.api.admin.code.StatsUnit;
import com.grepp.teamnotfound.app.controller.api.admin.payload.*;
import com.grepp.teamnotfound.app.model.board.dto.MonthlyArticlesStatsDto;
import com.grepp.teamnotfound.app.model.board.dto.YearlyArticlesStatsDto;
import com.grepp.teamnotfound.app.model.report.ReportService;
import com.grepp.teamnotfound.app.model.report.dto.ReportDetailDto;
import com.grepp.teamnotfound.app.model.report.dto.ReportsListDto;
import com.grepp.teamnotfound.app.model.user.AdminService;
import com.grepp.teamnotfound.app.model.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    @Operation(summary = "전체 가입자 수 조회")
    @GetMapping("v1/stats/users")
    public ResponseEntity<UserCountResponse> getUsersCount(){
        TotalUsersDto dto = adminService.getTotalUsersCount();
        return ResponseEntity.ok(UserCountResponse.from(dto));
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping("v1/users")
    public ResponseEntity<UsersListResponse> getUsers(
            @Valid @ModelAttribute UsersListRequest request){
        Page<UsersListDto> userPage = adminService.getUsersList(request);
        return ResponseEntity.ok(UsersListResponse.from(userPage));
    }

    @Operation(summary = "신고 내역 목록 조회")
    @GetMapping("v1/reports")
    public ResponseEntity<ReportsListResponse> getReports(
            @Valid @ModelAttribute ReportsListRequest request){
        Page<ReportsListDto> reportPage = adminService.getReportsList(request);
        return ResponseEntity.ok(ReportsListResponse.of(reportPage));
    }

    @Operation(summary = "가입/탈퇴자 수 추이 조회")
    @GetMapping("v1/stats/transition")
    public ResponseEntity<UserStatsResponse> userStatsList(
            @RequestParam(defaultValue = "MONTH") StatsUnit unit){
        if (unit == StatsUnit.MONTH) {
            List<MonthlyUserStatsDto> monthlyStats = adminService.getMonthlyUsersStats();
            return ResponseEntity.ok(
                    UserStatsResponse.<MonthlyUserStatsDto>builder()
                            .viewDate(OffsetDateTime.now())
                            .stats(monthlyStats)
                            .build());
        }
        else {
            List<YearlyUserStatsDto> yearlyStats = adminService.getYearlyUsersStats();
            return ResponseEntity.ok(
                    UserStatsResponse.<YearlyUserStatsDto>builder()
                            .viewDate(OffsetDateTime.now())
                            .stats(yearlyStats)
                            .build());
        }
    }

    @Operation(summary = "게시글 수 추이 조회")
    @GetMapping("v1/stats/articles")
    public ResponseEntity<ArticlesStatsResponse> articlesStatsList(
            @RequestParam(defaultValue = "MONTH") StatsUnit unit){
        if (unit == StatsUnit.MONTH) {
            List<MonthlyArticlesStatsDto> monthlyStats = adminService.getMonthlyArticlesStats();
            return ResponseEntity.ok(
                    ArticlesStatsResponse.<MonthlyArticlesStatsDto>builder()
                            .viewDat(OffsetDateTime.now())
                            .stats(monthlyStats)
                            .build());
        }
        else {
            List<YearlyArticlesStatsDto> yearlyStats = adminService.getYearlyArticlesStats();
            return ResponseEntity.ok(
                    ArticlesStatsResponse.<YearlyArticlesStatsDto>builder()
                            .viewDat(OffsetDateTime.now())
                            .stats(yearlyStats)
                            .build());
        }
    }

    @Operation(summary = "신고내역 상세 보기")
    @GetMapping("v1/reports/{reportId}")
    public ResponseEntity<ReportDetailResponse> getReportDetail(@PathVariable Long reportId) {
        ReportDetailDto dto = reportService.getReportDetail(reportId);
        return ResponseEntity.ok(ReportDetailResponse.from(dto));
    }

    @Operation(summary = "신고 처리하기")
    @PatchMapping("v1/reports/result-accept")
    public ResponseEntity<?> acceptReport(@RequestBody AcceptReportRequest request){
        AcceptReportDto dto = AcceptReportDto.from(request);
        adminService.acceptReportAndSuspendUser(dto);
        return ResponseEntity.ok("신고가 정상적으로 처리되었습니다.");
    }

    @Operation(summary = "신고 철회하기")
    @PatchMapping("v1/reports/result-reject")
    public ResponseEntity<?> rejectReport(@RequestBody RejectReportRequest request){
        RejectReportDto dto = RejectReportDto.from(request);
        adminService.rejectReport(dto);
        return ResponseEntity.ok("신고를 성공적으로 거절하였습니다.");
    }

}
