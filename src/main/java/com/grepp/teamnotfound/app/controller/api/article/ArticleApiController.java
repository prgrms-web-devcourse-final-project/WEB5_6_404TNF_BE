package com.grepp.teamnotfound.app.controller.api.article;

import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleDetailResponse;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleListRequest;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleListResponse;
import com.grepp.teamnotfound.app.controller.api.article.payload.ArticleRequest;
import com.grepp.teamnotfound.app.controller.api.article.payload.LikeResponse;
import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.board.ArticleService;
import com.grepp.teamnotfound.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/community/articles", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArticleApiController {

    private final ArticleService articleService;

    @GetMapping("/v1")
    @Operation(summary = "특정 게시판의 게시글 리스트 조회")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ResponseEntity<?> getAllArticles(
        @ModelAttribute @Valid ArticleListRequest request
    ) {
        ArticleListResponse response = articleService.getArticles(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 게시글 작성")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createArticle(
        @RequestPart("request") ArticleRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal Principal principal
    ) {
        Long articleId = articleService.writeArticle(request, images, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("articleId", articleId)));
    }

    @GetMapping("/v1/{articleId}")
    @Operation(summary = "게시글 상세 조회")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ResponseEntity<?> getArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal Principal principal
    ) {
        Long userId = null;
        if (principal != null) {
            userId = principal.getUserId();
        }

        ArticleDetailResponse response = articleService.findByArticleIdAndUserId(articleId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping( value = "/v1/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 수정")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateArticle(
        @PathVariable Long articleId,
        @RequestPart("request") ArticleRequest request,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @AuthenticationPrincipal Principal principal
    ) {
        articleService.updateArticle(articleId, request, images, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("result", "게시글이 정상적으로 수정되었습니다.")));
    }

    @DeleteMapping("/v1/{articleId}")
    @Operation(summary = "게시글 삭제")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal Principal principal
    ) {
        articleService.deleteArticle(articleId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("msg", "게시글이 정상적으로 삭제되었습니다.")));
    }

    @PostMapping("/v1/{articleId}/like")
    @Operation(summary = "게시글 좋아요 요청")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> likeArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal Principal principal
    ) {
        LikeResponse response = articleService.likeWithRedis(articleId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/v1/{articleId}/like")
    @Operation(summary = "게시글 좋아요 취소")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> undoLikeArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal Principal principal
    ) {
        LikeResponse response = articleService.unlikeWithRedis(articleId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/v1/{articleId}/reply")
    @Operation(summary = "게시글 댓글 개수")
    public ResponseEntity<?> getReplyCount(
        @PathVariable Long articleId
    ) {
        Integer replyCount = articleService.getReplyCount(articleId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("replies", replyCount)));
    }

    @GetMapping("/v1/{articleId}/like")
    @Operation(summary = "게시글 좋아요 개수")
    public ResponseEntity<?> getLikeCount(
        @PathVariable Long articleId
    ) {
        Integer likeCount = articleService.getActualLikeCount(articleId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("likes", likeCount)));
    }

    // TODO 게시글 신고 기능
}
