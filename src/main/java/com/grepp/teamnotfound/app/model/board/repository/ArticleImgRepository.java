package com.grepp.teamnotfound.app.model.board.repository;

import com.grepp.teamnotfound.app.model.board.entity.Article;
import com.grepp.teamnotfound.app.model.board.entity.ArticleImg;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ArticleImgRepository extends JpaRepository<ArticleImg, Long> {

    List<ArticleImg> findByArticle_ArticleId(Long articleId);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ArticleImg ai SET ai.deletedAt = :deletedAt, ai.updatedAt = :deletedAt WHERE ai.article.articleId = :articleId AND ai.deletedAt IS NULL ")
    void softDeleteByArticleId(@Param("articleId") Long articleId, @Param("deletedAt") OffsetDateTime deletedAt);
}
