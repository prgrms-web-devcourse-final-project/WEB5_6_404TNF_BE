package com.grepp.teamnotfound.app.model.report.code;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;
import com.grepp.teamnotfound.app.model.reply.repository.ReplyRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.BoardErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ReplyErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {

    BOARD("게시글") {
        @Override
        public void processReport(Long contentId,
                                  ArticleRepository articleRepository,
                                  ReplyRepository replyRepository) {
            Article article = articleRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(BoardErrorCode.ARTICLE_NOT_FOUND));
            if (article.getReportedAt() == null) {
                article.report();
            }
        }
    },
    REPLY("댓글") {
        @Override
        public void processReport(Long contentId,
                                  ArticleRepository articleRepository,
                                  ReplyRepository replyRepository) {
            Reply reply = replyRepository.findById(contentId)
                    .orElseThrow(() -> new BusinessException(ReplyErrorCode.REPLY_NOT_FOUND));
            if (reply.getReportedAt() == null) {
                reply.report();
            }
        }
    };

    private final String description;

    // enum 값 각각이 구현해야 할 추상 메서드
    public abstract void processReport(Long contentId,
                                       ArticleRepository articleRepository,
                                       ReplyRepository replyRepository);
}