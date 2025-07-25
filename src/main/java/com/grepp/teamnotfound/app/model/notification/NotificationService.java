package com.grepp.teamnotfound.app.model.notification;

import com.grepp.teamnotfound.app.model.notification.code.NotiTarget;
import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import com.grepp.teamnotfound.app.model.notification.dto.NotiReadDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserDto;
import com.grepp.teamnotfound.app.model.notification.dto.NotiUserSettingDto;
import com.grepp.teamnotfound.app.model.notification.entity.NotiManagement;
import com.grepp.teamnotfound.app.model.notification.entity.ScheduleNoti;
import com.grepp.teamnotfound.app.model.notification.entity.ServiceNoti;
import com.grepp.teamnotfound.app.model.notification.repository.NotiManagementRepository;
import com.grepp.teamnotfound.app.model.notification.repository.ScheduleNotiRepository;
import com.grepp.teamnotfound.app.model.notification.repository.ServiceNotiRepository;
import com.grepp.teamnotfound.app.model.schedule.repository.ScheduleRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.NotificationErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final UserRepository userRepository;
    private final NotiManagementRepository notiManagementRepository;
    private final ScheduleNotiRepository scheduleNotiRepository;
    private final ServiceNotiRepository serviceNotiRepository;
    private final ScheduleRepository scheduleRepository;

    ModelMapper modelMapper = new ModelMapper();

    public List<NotiUserDto> getUserNoti(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        List<NotiUserDto> result = new ArrayList<>();

        // 스케줄 알림
        LocalDate startMonth = LocalDate.now().minusMonths(1);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<ScheduleNoti> scheduleNotis = scheduleNotiRepository.getAllNoti(user.getUserId(), startMonth, tomorrow);
        for (ScheduleNoti sn : scheduleNotis) {
            result.add(NotiUserDto.builder()
                .notiId(sn.getScheduleNotiId())
                .content(sn.getContent())
                .type(NotiType.SCHEDULE)
                .targetId(sn.getSchedule().getScheduleId())
                .isRead(sn.getIsRead())
                .createdAt(sn.getCreatedAt())
                .build());
        }

        // 서비스 알림
        OffsetDateTime monthBefore = OffsetDateTime.now().minusMonths(1);

        List<ServiceNoti> serviceNotis = serviceNotiRepository.getAllNoti(user.getUserId(), monthBefore);
        for (ServiceNoti sn : serviceNotis) {
            result.add(NotiUserDto.builder()
                .notiId(sn.getServiceNotiId())
                .content(sn.getContent())
                .type(sn.getNotificationType())
                .targetId(sn.getTargetId())
                .isRead(sn.getIsRead())
                .createdAt(sn.getCreatedAt())
                .build());
        }

        // 최신순 정렬
        result.sort(Comparator.comparing(NotiUserDto::getCreatedAt).reversed());

        return result;
    }

    @Transactional
    public NotiUserSettingDto getUserSetting(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        NotiManagement noti = notiManagementRepository.findByUser(user)
            .orElseGet(() -> {
                log.warn("회원가입 당시 NotiManagement 미생성 오류 지금 생성 작업 진행 userId: {}", userId);

                NotiManagement created = new NotiManagement();
                created.setUser(user);

                return notiManagementRepository.save(created);
            });

        NotiUserSettingDto dto = modelMapper.map(noti, NotiUserSettingDto.class);
        return dto;
    }

    @Transactional
    public void createManagement(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        NotiManagement noti = new NotiManagement();
        noti.setUser(user);

        notiManagementRepository.save(noti);
    }

    @Transactional
    public NotiUserSettingDto changeNotiSetting(Long userId, NotiTarget target) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        NotiManagement noti = notiManagementRepository.findByUser(user)
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_MANAGEMENT_NOT_FOUND));

        if (target.equals(NotiTarget.SERVICE)) {
            noti.setIsNotiService(!noti.getIsNotiService());
        } else if (target.equals(NotiTarget.SCHEDULE)) {
            noti.setIsNotiSchedule(!noti.getIsNotiSchedule());
        } else {
            Boolean allNotiState = noti.getIsNotiAll();
            noti.setIsNotiService(!allNotiState);
            noti.setIsNotiSchedule(!allNotiState);
            noti.setIsNotiAll(!allNotiState);
        }
        noti.setUpdatedAt(OffsetDateTime.now());

        notiManagementRepository.save(noti);

        NotiUserSettingDto dto = modelMapper.map(noti, NotiUserSettingDto.class);
        return dto;
    }

    private static final Set<NotiType> SERVICE_TYPES = EnumSet.of(
        NotiType.LIKE,
        NotiType.COMMENT,
        NotiType.RECOMMEND,
        NotiType.REPORT_SUCCESS,
        NotiType.REPORT_FAIL,
        NotiType.REPORTED
    );

    @Transactional
    public NotiReadDto readNoti(Long notiId, NotiType type) {
        NotiReadDto dto = new NotiReadDto();

        if (type == NotiType.SCHEDULE) {
            ScheduleNoti scheduleNoti = scheduleNotiRepository.findById(notiId)
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

            scheduleNoti.setIsRead(true);
            scheduleNoti.setUpdatedAt(OffsetDateTime.now());

            dto.setType(NotiType.SCHEDULE);
            dto.setTargetId(scheduleNoti.getSchedule().getScheduleId());

        } else if (SERVICE_TYPES.contains(type)) {
            ServiceNoti serviceNoti = serviceNotiRepository.findById(notiId)
                .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

            serviceNoti.setIsRead(true);
            serviceNoti.setUpdatedAt(OffsetDateTime.now());

            dto.setType(serviceNoti.getNotificationType());
            dto.setTargetId(serviceNoti.getTargetId());

        } else {
            throw new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }

        return dto;
    }


    @Transactional
    public void deleteNoti(Long notiId, NotiType type) {
        if (type == NotiType.SCHEDULE) {

            scheduleNotiRepository.deleteOne(notiId);

        } else if (SERVICE_TYPES.contains(type)) {

            serviceNotiRepository.deleteOne(notiId);

        } else {
            throw new BusinessException(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteAllNoti(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        scheduleNotiRepository.deleteAllByUser(user.getUserId());
        serviceNotiRepository.deleteAllByUser(user.getUserId());
    }
}
