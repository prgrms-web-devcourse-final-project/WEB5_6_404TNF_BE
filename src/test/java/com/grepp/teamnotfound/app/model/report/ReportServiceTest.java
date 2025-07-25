package com.grepp.teamnotfound.app.model.report;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.entity.Board;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.dto.ReportDetailDto;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.report.repository.ReportRepository;
import com.grepp.teamnotfound.app.model.user.code.UserStateResponse;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReportErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ReplyRepository replyRepository;

    // 공통 객체
    private User reporter;
    private User reported;
    private Board board;
    private Article article;
    private Reply reply;

    @BeforeEach
    void setUp() {
        reporter = User.builder()
                .userId(1L)
                .nickname("신고자")
                .build();
        reported = User.builder()
                .userId(2L)
                .nickname("피신고자")
                .build();
        board = Board.builder()
                .boardId(1L)
                .name("자유게시판")
                .build();
        article = Article.builder()
                .articleId(10L)
                .title("테스트 게시글")
                .board(board)
                .build();
        reply = Reply.builder()
                .replyId(1L)
                .content("테스트 댓글")
                .article(article)
                .build();
    }

    @Test
    @DisplayName("성공 - 게시글 신고 상세 조회/fetch 진행 여부")
    void getReportDetail_forBoard_success() {
        // given
        Long reportId = 1L;
        Long contentId = article.getArticleId();
        Report boardReport = Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reported)
                .type(ReportType.BOARD)
                .contentId(contentId)
                .build();

        // report 존재 검증
        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.of(boardReport));
        // 신고된 article 존재 검증
        when(articleRepository.findWithBoardByArticleId(contentId))
                .thenReturn(Optional.of(article));

        // when
        ReportDetailDto result = reportService.getReportDetail(reportId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReportId()).isEqualTo(reportId);
        assertThat(result.getContentId()).isEqualTo(contentId);
        assertThat(result.getArticleId()).isEqualTo(article.getArticleId());
        assertThat(result.getBoardType()).isEqualTo(board.getName());
        assertThat(result.getReporterNickname()).isEqualTo(reporter.getNickname());
        assertThat(result.getReportedNickname()).isEqualTo(reported.getNickname());
        assertThat(result.getReportedState()).isEqualTo(reported.getUserState());
        assertThat(result.getReportedState()).isEqualTo(UserStateResponse.ACTIVE);

        // replyRepository의 메소드는 호출되지 않았는지
        verify(replyRepository, never()).findArticleWithBoardByReplyId(anyLong());
    }

    @Test
    @DisplayName("성공 - 댓글 신고 상세 조회/fetch 진행 여부")
    void getReportDetail_forReply_success() {
        // given
        Long reportId = 2L;
        Long contentId = 20L;       // reply id
        Report replyReport = Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reported)
                .type(ReportType.REPLY)
                .contentId(contentId)
                .build();

        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.of(replyReport));
        when(replyRepository.findArticleWithBoardByReplyId(contentId))
                .thenReturn(Optional.of(article));

        // when
        ReportDetailDto result = reportService.getReportDetail(reportId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(replyReport.getType());
        assertThat(result.getBoardType()).isEqualTo(board.getName());

        verify(articleRepository, never()).findWithBoardByArticleId(anyLong());
    }

    @Test
    @DisplayName("실패 - report not found")
    void getReportDetail_reportNotFound() {
        // given
        Long reportId = 999L;
        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.empty());

        // when then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reportService.getReportDetail(reportId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ReportErrorCode.REPORT_NOT_FOUND);
    }

    @Test
    @DisplayName("실패 - article not found - BOARD")
    void getReportDetail_forBoard_articleNotFound() {
        // given
        Long reportId = 1L;
        Long contentId = article.getArticleId();
        Report boardReport = Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reported)
                .type(ReportType.BOARD)
                .contentId(contentId)
                .build();

        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.of(boardReport));
        when(articleRepository.findWithBoardByArticleId(contentId))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reportService.getReportDetail(reportId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(BoardErrorCode.ARTICLE_NOT_FOUND);
        verify(replyRepository, never()).findArticleWithBoardByReplyId(anyLong());
    }

    @Test
    @DisplayName("실패 - article not found - REPLY")
    void getReportDetail_forReply_articleNotFound() {
        // given
        Long reportId = 1L;
        Long contentId = article.getArticleId();
        Report replyReport = Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reported)
                .type(ReportType.REPLY)
                .contentId(contentId)
                .build();

        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.of(replyReport));
        when(replyRepository.findArticleWithBoardByReplyId(contentId))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            reportService.getReportDetail(reportId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(BoardErrorCode.ARTICLE_NOT_FOUND);
        verify(articleRepository, never()).findWithBoardByArticleId(anyLong());
    }

    @Test
    @DisplayName("성공 - 대상자가 탈퇴일 때 해당 UserState = LEAVE")
    void getReportDetail_whenReportedUserIsLeave() {
        // given
        User reportedLeaveUser = User.builder()
                .userId(1L)
                .nickname("탈퇴한대상자")
                .build();
        reportedLeaveUser.setDeletedAt(OffsetDateTime.now().minusDays(1));

        Long reportId = 1L;
        Long contentId = article.getArticleId();
        Report report = Report.builder()
                .reportId(reportId)
                .reporter(reporter)
                .reported(reportedLeaveUser)
                .type(ReportType.BOARD)
                .contentId(contentId)
                .build();


        when(reportRepository.findByReportIdWithUsers(reportId))
                .thenReturn(Optional.of(report));
        when(articleRepository.findWithBoardByArticleId(contentId))
                .thenReturn(Optional.of(article));

        // when
        ReportDetailDto result = reportService.getReportDetail(reportId);

        // then
        assertThat(result.getReportedState()).isEqualTo(UserStateResponse.LEAVE);
    }
}