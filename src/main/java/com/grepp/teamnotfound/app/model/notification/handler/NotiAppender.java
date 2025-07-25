package com.grepp.teamnotfound.app.model.notification.handler;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiBasicDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiScheduleCreateDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiServiceCreateDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserDto;
import com.grepp.teamnotfound.app.model.notification.entity.NotiManagement;
import com.grepp.teamnotfound.app.model.notification.repository.EmitterRepository;
import com.grepp.teamnotfound.app.model.notification.repository.NotiManagementRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotiAppender {

    private final UserRepository userRepository;
    private final NotiManagementRepository notiManagementRepository;
    private final ScheduleNotiHandlerImpl scheduleNotiHandlerImpl;
    private final ServiceNotiHandlerImpl serviceNotiHandlerImpl;
    private final EmitterRepository emitterRepository;

    public void append(Long userId, NotiType notiType, NotiBasicDto dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        NotiManagement notiManagement = notiManagementRepository.findByUser(user)
            .orElseGet(() -> {
                log.warn("회원가입 당시 NotiManagement 미생성 오류 지금 생성 작업 진행 userId: {}", userId);

                NotiManagement created = new NotiManagement();
                created.setUser(user);

                return notiManagementRepository.save(created);
            });

        if (!notiManagement.getIsNotiAll()) {
            return;
        }

        if (notiType == NotiType.SCHEDULE && !notiManagement.getIsNotiSchedule()) {
            return;
        }

        if (notiType != NotiType.SCHEDULE && !notiManagement.getIsNotiService()) {
            return;
        }

        NotiUserDto notiDto;

        // NOTE 스케줄 알림 생성
        if (notiType.equals(NotiType.SCHEDULE)) {
            // NOTE 스케줄 알림 생성
            notiDto = scheduleNotiHandlerImpl.handle(user, (NotiScheduleCreateDto) dto);
        } else {
            // NOTE 서비스 알림 생성
            // 알림 받을 user, 알림 타입, 알림 dto
            notiDto = serviceNotiHandlerImpl.handle(user, notiType, (NotiServiceCreateDto) dto);
        }

        // SSE 전송
        emitterRepository.get(userId).ifPresent(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("noti")
                    .data(notiDto));
                log.info("✅ 알림 전송 성공 - userId: {}, data: {}", userId, notiDto);
            } catch (IOException e) {
                log.error("❌ 알림 전송 실패 - userId: {}", userId, e);
                emitterRepository.delete(userId);
            }

        });
    }
}

