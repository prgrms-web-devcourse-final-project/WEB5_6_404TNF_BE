package com.grepp.teamnotfound.app.model.schedule;

import com.grepp.teamnotfound.app.controller.api.schedule.payload.ScheduleCreateRequest;
import com.grepp.teamnotfound.app.controller.api.schedule.payload.ScheduleEditRequest;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleCreateRequestDto;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleEditRequestDto;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import com.grepp.teamnotfound.app.model.schedule.repository.ScheduleRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.PetException;
import com.grepp.teamnotfound.infra.error.exception.ScheduleException;
import com.grepp.teamnotfound.infra.error.exception.UserException;
import com.grepp.teamnotfound.infra.error.exception.code.PetErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.ScheduleErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 한달치 일정 조회
    public List<Schedule> getCalendar(Long petId, LocalDate date) {
        // petId 존재 검증
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));

        YearMonth ym = YearMonth.from(date);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return scheduleRepository.findByPetAndScheduleDateBetweenAndDeletedAtNull(pet, start, end);
    }

    // 일정 등록(생성)
    @Transactional
    public void createSchedule(ScheduleCreateRequestDto request){
        // petId, userId 존재 검증
        Pet pet = petRepository.findById(request.getPetId()).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (request.getCycle() == null) {
            Schedule schedule = Schedule.builder()
                    .name(request.getName())
                    .scheduleDate(request.getDate())
                    .cycle(request.getCycle())
                    .cycleEnd(request.getCycleEnd())
                    .isDone(false)
                    .pet(pet)
                    .user(user).build();
            scheduleRepository.save(schedule);
        }else{
            for(LocalDate date = request.getDate(); date.isBefore(request.getCycleEnd()); date = date.plusDays(request.getCycle().getDays(request.getDate()))){
                Schedule schedule = Schedule.builder()
                        .name(request.getName())
                        .scheduleDate(date)
                        .cycle(request.getCycle())
                        .cycleEnd(request.getCycleEnd())
                        .isDone(false)
                        .pet(pet)
                        .user(user).build();
                scheduleRepository.save(schedule);
            }
        }

    }

    // 일정 수정
    @Transactional
    public void editSchedule(ScheduleEditRequestDto request){
        // petId, userId 존재 여부 검증
        Pet pet = petRepository.findById(request.getPetId()).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 일정 존재 여부 검증
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        // 사이클 전체 or 해당 일정만 수정
        if (request.getCycleLink()){
            List<Schedule> schedules = scheduleRepository.findByNameAndCycleAndCycleEnd(schedule.getName(), schedule.getCycle(), schedule.getCycleEnd());
            LocalDate date = request.getDate();
            for(Schedule schedule1: schedules){
                schedule1.setName(request.getName());
                schedule1.setScheduleDate(date);
                schedule1.setCycle(request.getCycle());
                schedule1.setCycleEnd(request.getCycleEnd());

                if (date.isAfter(request.getCycleEnd())){ // 범위를 벗어나면 삭제
                    schedule1.setDeletedAt(OffsetDateTime.now());
                }else {
                    schedule1.setUpdatedAt(OffsetDateTime.now());
                }

                date = date.plusDays(request.getCycle().getDays(date));
            }
            scheduleRepository.saveAll(schedules);
        }else {
            schedule.setName(request.getName());
            schedule.setScheduleDate(request.getDate());
            schedule.setUpdatedAt(OffsetDateTime.now());
            scheduleRepository.save(schedule);
        }
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId, Boolean cycleLink){
        // userId 존재 여부 검증
        userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        // 사이클 전체 or 해당 일정만 삭제
        if (cycleLink){
            List<Schedule> schedules = scheduleRepository.findByNameAndCycleAndCycleEnd(schedule.getName(), schedule.getCycle(), schedule.getCycleEnd());
            schedules.forEach(schedule1 -> {
                schedule1.setDeletedAt(OffsetDateTime.now());
            });
            scheduleRepository.saveAll(schedules);
        }else {
            schedule.setDeletedAt(OffsetDateTime.now());
            scheduleRepository.save(schedule);
        }
    }

    public void checkIsDone(Long petId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        schedule.setIsDone(!schedule.getIsDone());
        scheduleRepository.save(schedule);
    }
}
