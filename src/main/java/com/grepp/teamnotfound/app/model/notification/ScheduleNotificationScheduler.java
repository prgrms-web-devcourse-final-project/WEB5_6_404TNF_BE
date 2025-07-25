package com.grepp.teamnotfound.app.model.notification;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiScheduleCreateDto;
import com.grepp.teamnotfound.app.model.notification.handler.NotiAppender;
import com.grepp.teamnotfound.app.model.notification.repository.ScheduleNotiRepository;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import com.grepp.teamnotfound.app.model.schedule.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduleNotificationScheduler {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleNotiRepository scheduleNotiRepository;
    private final NotiAppender notiAppender;

    @Scheduled(cron = "0 50 11 * * *", zone = "Asia/Seoul") // 매일 오후 11시 50분
//    @Scheduled(cron = "*/10 * * * * *") // 10초마다
    @Transactional
    public void createScheduleNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Schedule> schedules = scheduleRepository.findSchedulesForNotification(tomorrow);
        log.info("✅ Found {} schedules", schedules.size());

        for (Schedule schedule1 : schedules) {
            try {
                boolean exists = scheduleNotiRepository.existsByScheduleId(schedule1.getScheduleId());
                if (exists) {
                    continue;
                }

                NotiScheduleCreateDto dto = NotiScheduleCreateDto.builder()
                    .scheduleId(schedule1.getScheduleId())
                    .scheduleDate(schedule1.getScheduleDate())
                    .build();

                notiAppender.append(
                    schedule1.getPet().getUser().getUserId(),
                    NotiType.SCHEDULE,
                    dto
                );

            } catch (Exception e) {
                log.error("스케줄 알림 생성 실패 - scheduleId: {}", schedule1.getScheduleId(), e);
            }
        }
    }

}
