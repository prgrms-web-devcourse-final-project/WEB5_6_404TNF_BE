package com.grepp.teamnotfound.app.model.board;

import com.grepp.teamnotfound.app.model.board.entity.ArticleLike;
import com.grepp.teamnotfound.app.model.board.repository.ArticleLikeRepository;
import com.grepp.teamnotfound.app.model.board.repository.ArticleRepository;
import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiServiceCreateDto;
import com.grepp.teamnotfound.app.model.notification.handler.NotiAppender;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final UserRepository userRepository;
    private final RedisLikeService redisLikeService;
    private final NotiAppender notiAppender;

    // articleId 별로 좋아요 처리 트랜잭션을 분리
    @Transactional
    public void syncOneArticleLikes(Long articleId) {
        Set<Object> likeRequests = redisLikeService.getAllLikeRequests(articleId);
        Set<Object> unlikeRequests = redisLikeService.getAllUnlikeRequests(articleId);

        // 요청 송신자 userId 리스트
        List<Long> usersToLike = likeRequests.stream()
            .map(object -> Long.valueOf(object.toString()))
            .toList();

        List<Long> usersToUnlike = unlikeRequests.stream()
            .map(object -> Long.valueOf(object.toString()))
            .toList();

        if (!usersToLike.isEmpty()) {
            List<Long> alreadyLiked = articleLikeRepository.findUserIdsByArticleId(articleId);

            List<ArticleLike> likesToInsert = usersToLike.stream()
                .filter(userId -> !alreadyLiked.contains(userId))
                .map(userId -> ArticleLike.builder()
                    .article(articleRepository.getReferenceById(articleId))
                    .user(userRepository.getReferenceById(userId))
                    .createdAt(OffsetDateTime.now())
                    .build()
                ).toList();

            articleLikeRepository.saveAll(likesToInsert);

            for (ArticleLike like : likesToInsert) {
                Long senderId = like.getUser().getUserId();
                Long receiverId = like.getArticle().getUser().getUserId();
                Long targetId = like.getLikeId();

                if (!senderId.equals(receiverId)) {
                    NotiServiceCreateDto dto = NotiServiceCreateDto.builder()
                        .targetId(targetId)
                        .notiType(NotiType.LIKE)
                        .build();

                    notiAppender.append(receiverId, NotiType.LIKE, dto);
                }
            }
        }

        // 좋아요를 한꺼번에 DELETE
        if (!usersToUnlike.isEmpty()) {
            // NOTE 나중에 성능이 안나온다면 JPQL 쿼리로 직접 DELETE 쿼리 날려보기
            List<ArticleLike> likesToDelete = articleLikeRepository.findAllByArticleIdAndUserIds(
                articleId, usersToUnlike);
            articleLikeRepository.deleteAllInBatch(likesToDelete);
        }

        // DB 에 최종 반영된 좋아요 수를 가져와 Redis 캐시를 업데이트하여 정합성 유지
        Integer finalDbLikeCount = articleLikeRepository.countByArticle_ArticleId(articleId);
        redisLikeService.setArticleLikesCount(articleId, finalDbLikeCount.longValue());

        // 모든 DB 작업이 성공하면 Redis 데이터 삭제
        redisLikeService.clearLikeRequests(articleId);
        redisLikeService.clearUnlikeRequests(articleId);
        redisLikeService.clearChangedArticleId(articleId);
    }
}
