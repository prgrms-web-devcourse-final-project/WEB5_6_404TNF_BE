package com.grepp.teamnotfound.app.model.reply.repository;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.reply.entity.Reply;

import java.time.OffsetDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Reply r SET r.deletedAt = :deletedAt, r.updatedAt = :deletedAt WHERE r.article.articleId = :articleId AND r.deletedAt IS NULL")
    void softDeleteByArticleId(@Param("articleId") Long articleId, @Param("deletedAt") OffsetDateTime deletedAt);

    Integer countByArticle_ArticleIdAndDeletedAtIsNullAndReportedAtIsNull(Long articleId);

    Page<Reply> findByArticle_ArticleIdAndDeletedAtIsNullAndReportedAtIsNull(Long articleId, Pageable pageable);


    @Query("select r.article from Reply r where r.replyId = :replyId")
    Optional<Article> findArticleByReplyId(@Param("replyId") Long replyId);

    @Query("""
            select a
            from Reply r
            join r.article a
            join fetch a.board
            where r.replyId = :replyId""")
    Optional<Article> findArticleWithBoardByReplyId(@Param("replyId") Long replyId);

    @Query("select a from Reply a join fetch a.user where a.replyId = :replyId")
    Optional<Reply> findByIdFetchUser(@Param("replyId") Long replyId);

    Optional<Reply> findByReplyId(Long replyId);
}
