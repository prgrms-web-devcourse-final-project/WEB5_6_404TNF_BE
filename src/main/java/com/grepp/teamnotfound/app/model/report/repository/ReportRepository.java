package com.grepp.teamnotfound.app.model.report.repository;

import com.grepp.teamnotfound.app.model.report.code.ReportCategory;
import com.grepp.teamnotfound.app.model.report.code.ReportState;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    @Query("select count(r) > 0 " +
            "from Report r " +
            "where r.reporter = :reporter " +
            "and r.type = :type " +
            "and r.contentId = :contentId")
    boolean duplicateReport(@Param("reporter") User reporter, @Param("type") ReportType reportType, @Param("contentId") Long contentId);

    @Query("select r from Report r where r.contentId = :contentId and r.category = :category and r.type =:reportType and r.state = :reportState")
    List<Report> findByContentIdAndReportCategoryAndReportTypeState(@Param("contentId") Long contentId,
                                                                    @Param("category") ReportCategory category,
                                                                    @Param("reportType") ReportType reportType,
                                                                    @Param("reportState") ReportState reportState);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Report r set r.state = :reportState, r.adminReason = :adminReason " +
            "where r.contentId = :contentId and r.category = :category and r.state = :currentState")
    void bulkRejectPendingReports(@Param("contentId") Long contentId, @Param("category") ReportCategory category,
                                 @Param("reportState") ReportState reportState, @Param("adminReason") String adminReason, @Param("currentState") ReportState currentState);

    @Query ("select r " +
            "from Report r " +
            "join fetch r.reporter " +
            "join fetch r.reported " +
            "where r.reportId = :reportId")
    Optional<Report> findByReportIdWithUsers(@Param("reportId") Long reportId);
}
