package com.grepp.teamnotfound.app.model.board.repository;

import com.grepp.teamnotfound.app.model.board.entity.ArticleLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ArticleLike al WHERE al.article.articleId = :articleId")
    void hardDeleteByArticleId(@Param("articleId") Long articleId);

    Optional<ArticleLike> findByArticle_ArticleIdAndUser_UserId(Long articleId, Long userId);

    Integer countByArticle_ArticleId(Long articleId);

    boolean existsByArticle_ArticleIdAndUser_UserId(Long article, Long userId);

    @Query("SELECT al.user.userId FROM ArticleLike al WHERE al.article.articleId = :articleId")
    List<Long> findUserIdsByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT al FROM ArticleLike al WHERE al.article.articleId = :articleId AND al.user.userId IN :userIds")
    List<ArticleLike> findAllByArticleIdAndUserIds(@Param("articleId") Long articleId, @Param("userIds") List<Long> userIds);
}
