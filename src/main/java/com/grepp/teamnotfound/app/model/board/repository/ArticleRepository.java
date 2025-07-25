package com.grepp.teamnotfound.app.model.board.repository;

import com.grepp.teamnotfound.app.model.board.entity.Article;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

    Page<Article> findByDeletedAtIsNullAndReportedAtIsNull(Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.articleId = :articleId AND a.deletedAt IS NULL")
    Integer plusViewById(@Param("articleId") Long articleId);

    Integer countByArticleIdAndDeletedAtIsNullAndReportedAtIsNull(Long articleId);


    @Query("SELECT COUNT(a) FROM Article  a WHERE a.createdAt BETWEEN :start AND :end")
    int countArticlesBetween(OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT a.articleId FROM Article a WHERE a.deletedAt IS NULL AND a.reportedAt IS NULL ")
    List<Long> findAllArticleIds();

    @Query("select a from Article a join fetch a.user where a.articleId = :articleId")
    Optional<Article> findByIdFetchUser(@Param("articleId") Long articleId);

    Optional<Article> findByArticleId(Long articleId);

    @Query("""
            select a
            from Article a
            join fetch a.board
            where a.articleId = :articleId
            """)
    Optional<Article> findWithBoardByArticleId(@Param("articleId") Long articleId);
}
