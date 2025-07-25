package com.grepp.teamnotfound.app.model.report;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.app.model.report.code.ReportType;
import com.grepp.teamnotfound.app.model.report.dto.ReportCommand;
import com.grepp.teamnotfound.app.model.report.dto.ReportDetailDto;
import com.grepp.teamnotfound.app.model.report.entity.Report;
import com.grepp.teamnotfound.app.model.report.repository.ReportRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReplyErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReportErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ArticleRepository articleRepository;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ReportDetailDto getReportDetail(Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new BusinessException(ReportErrorCode.REPORT_NOT_FOUND));

        String reporterNickname = userRepository.findNicknameByUserId(report.getReporter().getUserId());

                Article article = (report.getType()==ReportType.REPLY) ?
                replyRepository.findArticleByReplyId(report.getContentId())
                        .orElseThrow(()-> new BusinessException(BoardErrorCode.ARTICLE_NOT_FOUND))
                : articleRepository.findByArticleId(report.getContentId())
                        .orElseThrow(()-> new BusinessException(BoardErrorCode.ARTICLE_NOT_FOUND));

        return ReportDetailDto.from(report, reporterNickname, article);
    }

    @Transactional
    public Long createReport(ReportCommand command) {
        User reporter = validateReporter(command.getReporterId());
        User reported = findReportedUser(command.getReportType(), command.getContentId());

        // 스스로 신고 불가
        validateSelfReport(reporter, reported);
        // 이미 본인이 신고한 경우 중복 신고 불가
        validateDuplicateReport(reporter, command);

        Report report = Report.of(command, reporter, reported);
        reportRepository.save(report);

        return report.getReportId();
    }

    @Transactional(readOnly = true)
    protected void validateDuplicateReport(User reporter, ReportCommand command) {
        if (reportRepository.duplicateReport(reporter, command.getReportType(), command.getContentId())) {
            throw new BusinessException(ReportErrorCode.DUPLICATED_REPORT);
        }
    }

    private void validateSelfReport(User reporter, User reported) {
        if (reporter.equals(reported)) {
            throw new BusinessException(ReportErrorCode.CANNOT_REPORT_SELF);
        }
    }

    @Transactional(readOnly = true)
    protected User findReportedUser(ReportType reportType, Long contentId) {
        if(reportType==ReportType.BOARD){
            // 게시글 존재 확인 및 작성자 갖고 오기
            Article article = articleRepository.findByIdFetchUser(contentId)
                    .orElseThrow(() -> new BusinessException(BoardErrorCode.ARTICLE_NOT_FOUND));
            return article.getUser();

        } else if(reportType==ReportType.REPLY){
            Reply reply = replyRepository.findByIdFetchUser(contentId)
                    .orElseThrow(() -> new BusinessException(ReplyErrorCode.REPLY_NOT_FOUND));
            return reply.getUser();

        } else throw new BusinessException(ReportErrorCode.REPORT_TYPE_BAD_REQUEST);
    }

    private User validateReporter(Long reporterId) {
        return userRepository.findByUserId(reporterId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
