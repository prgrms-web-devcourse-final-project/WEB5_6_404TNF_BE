package com.grepp.teamnotfound.app.controller.api.reply;

import com.grepp.teamnotfound.app.controller.api.reply.payload.ReplyDetailResponse;
import com.grepp.teamnotfound.app.controller.api.reply.payload.ReplyListRequest;
import com.grepp.teamnotfound.app.controller.api.reply.payload.ReplyListResponse;
import com.grepp.teamnotfound.app.controller.api.reply.payload.ReplyRequest;
import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.reply.ReplyService;
import com.grepp.teamnotfound.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/community/articles/{articleId}/replies", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReplyApiController {

    private final ReplyService replyService;

    @GetMapping("/v1")
    @Operation(summary = "특정 게시글의 댓글 리스트 조회")
    @PreAuthorize("isAnonymous() or isAuthenticated()")
    public ResponseEntity<?> getAllReplies(
        @PathVariable Long articleId,
        @ModelAttribute @Valid ReplyListRequest request
    ) {
        ReplyListResponse response = replyService.getReplies(articleId, request.getPage(), request.getSize());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/v1")
    @Operation(summary = "댓글 작성")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReply(
        @PathVariable Long articleId,
        @ModelAttribute ReplyRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        ReplyDetailResponse response = replyService.createReply(request, articleId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/v1/{replyId}")
    @Operation(summary = "댓글 수정")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateReply(
        @PathVariable Long articleId,
        @PathVariable Long replyId,
        @ModelAttribute ReplyRequest request,
        @AuthenticationPrincipal Principal principal
    ) {
        ReplyDetailResponse response = replyService.updateReply(request, articleId, replyId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/v1/{replyId}")
    @Operation(summary = "댓글 삭제")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteReply(
        @PathVariable Long articleId,
        @PathVariable Long replyId,
        @AuthenticationPrincipal Principal principal
    ) {
        replyService.deleteReply(articleId, replyId, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("result", "댓글이 정상적으로 삭제되었습니다.")));
    }

    // TODO 댓글 신고기능
}
