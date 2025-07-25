package com.grepp.teamnotfound.app.model.notification.handler;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiScheduleCreateDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserDto;
import com.grepp.teamnotfound.app.model.notification.entity.ScheduleNoti;
import com.grepp.teamnotfound.app.model.notification.repository.ScheduleNotiRepository;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import com.grepp.teamnotfound.app.model.schedule.repository.ScheduleRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleNotiHandlerImpl implements ScheduleNotiHandler {

    @Autowired
    private ScheduleNotiRepository scheduleNotiRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public NotiUserDto handle(User user, NotiScheduleCreateDto dto) {

        ScheduleNoti noti = new ScheduleNoti();

        Schedule schedule = scheduleRepository.findById(dto.getScheduleId()).get();
        String content = schedule.getName();
        LocalDate notiDate = schedule.getScheduleDate();

        if (notiDate.isBefore(LocalDate.now())) {
            return null;
        }
        noti.setContent("내일 " + content + " 일정이 예정되어있습니다.");
        noti.setUser(user);
        noti.setSchedule(schedule);
        noti.setNotiDate(notiDate);

        ScheduleNoti saved = scheduleNotiRepository.save(noti);

        return NotiUserDto.builder()
            .notiId(saved.getScheduleNotiId())
            .content(saved.getContent())
            .type(NotiType.SCHEDULE)
            .targetId(schedule.getScheduleId())
            .isRead(saved.getIsRead())
            .createdAt(saved.getCreatedAt())
            .build();
    }
}
