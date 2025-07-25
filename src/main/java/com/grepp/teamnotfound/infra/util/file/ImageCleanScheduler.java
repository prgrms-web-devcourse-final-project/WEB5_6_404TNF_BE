package com.grepp.teamnotfound.infra.util.file;

import com.grepp.teamnotfound.app.model.board.entity.ArticleImg;
import com.grepp.teamnotfound.app.model.board.repository.ArticleImgRepository;
import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.app.model.pet.repository.PetImgRepository;
import com.grepp.teamnotfound.app.model.user.entity.UserImg;
import com.grepp.teamnotfound.app.model.user.repository.UserImgRepository;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageCleanScheduler {

    private final ArticleImgRepository articleImgRepository;
    private final PetImgRepository petImgRepository;
    private final UserImgRepository userImgRepository;
    private final GoogleStorageManager fileManager;

    // 소프트 딜리트 후 보존 기간
    @Value("${image.clean.period-days}")
    private int softDeletePeriodDays;

    // 매일 오전 1시에 실행
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    @Transactional
    public void cleanSoftDeletedImages() {
        log.info("Start image clean up scheduler...");

        OffsetDateTime targetDate = OffsetDateTime.now().minusDays(softDeletePeriodDays);
        OffsetDateTime startOfTargetDay = targetDate.with(LocalTime.MIN);
        OffsetDateTime endOfTargetDay = targetDate.with(LocalTime.MAX);

        List<ArticleImg> articleImgsToClean = articleImgRepository.findByDeletedAtBetween(startOfTargetDay, endOfTargetDay);
        cleanImages(articleImgsToClean, "article");

        List<PetImg> petImgsToClean = petImgRepository.findByDeletedAtBetween(startOfTargetDay, endOfTargetDay);
        cleanImages(petImgsToClean, "pet");

        List<UserImg> userImgsToClean = userImgRepository.findByDeletedAtBetween(startOfTargetDay, endOfTargetDay);
        cleanImages(userImgsToClean, "user");

        log.info("Finish image clean up scheduler.");
    }

    private <T extends ImageFile> void cleanImages(
        List<T> imgsToClean,
        String entityName
    ) {
        if (!imgsToClean.isEmpty()) {
            List<FileDto> fileDtos = imgsToClean.stream()
                .map(ImageFile::toFileDto)
                .toList();

            fileManager.delete(fileDtos);
            log.info("Image clean up processing finished for {} {}.", imgsToClean.size(), entityName);
        } else {
            log.info("No {} images to clean up.", entityName);
        }
    }
}
