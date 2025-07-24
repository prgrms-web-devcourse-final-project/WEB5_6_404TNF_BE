package com.grepp.teamnotfound.app.controller.api.report;

import com.grepp.teamnotfound.app.controller.api.report.payload.ReportRequest;
import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.report.ReportService;
import com.grepp.teamnotfound.app.model.report.dto.ReportCommand;
import com.grepp.teamnotfound.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/reports")
public class ReportApiController {

    private final ReportService reportService;

    @PostMapping("/v1")
    @Operation(summary = "커뮤니티 게시글/댓글 신고")
    public ResponseEntity<?> createReport(@RequestBody ReportRequest request,
                                          @AuthenticationPrincipal Principal principal) {

        ReportCommand command = ReportCommand.from(principal, request);
        Long createId = reportService.createReport(command);
        return ResponseEntity.ok(ApiResponse.success(createId));
    }

}
