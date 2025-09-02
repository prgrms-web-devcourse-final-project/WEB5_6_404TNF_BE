package com.grepp.teamnotfound.app.model.board;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeBatchProcessor {

    private final RedisLikeService redisLikeService;
    private final LikeService likeService;
    private final RedissonClient redissonClient;
    private static final String BATCH_LOCK_KEY = "lock:like_batch_processor";

    // 1분마다 실행
    @Scheduled(fixedDelay = 60000)
    public void processLikeBatch() {
        RLock lock = redissonClient.getLock(BATCH_LOCK_KEY);
        try {
            boolean isLocked = lock.tryLock(10, 55, TimeUnit.SECONDS);

            if (isLocked) {
                log.info("Acquired lock. Starting likes batch processing...");
                syncLikesWithDB();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released");
            }
        }
    }

    // 좋아요 관련 요청을 DB에 반영
    public void syncLikesWithDB() {
        Set<Long> changedArticleIds = redisLikeService.getAllChangedArticleIdsAndClear();

        if (changedArticleIds.isEmpty()) {
            log.info("No changed article found. Skipping batch processing.");
            return;
        }

        for (Long articleId : changedArticleIds) {
            try {
                likeService.syncOneArticleLikes(articleId);
            } catch (Exception e) {
                log.error("Failed to sync likes for articleId: {}", articleId);
            }
        }

        log.info("Likes batch processing finished for {} articles.", changedArticleIds.size());
    }
}
