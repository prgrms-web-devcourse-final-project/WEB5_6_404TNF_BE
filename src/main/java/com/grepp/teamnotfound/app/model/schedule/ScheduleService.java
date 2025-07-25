package com.grepp.teamnotfound.app.model.schedule;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.schedule.code.ScheduleCycle;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleCreateRequestDto;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleDto;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // 한달치 일정 조회
    @Transactional
    public List<ScheduleDto> getCalendar(Long userId, LocalDate date) {
        // user 검증
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        YearMonth ym = YearMonth.from(date);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Schedule> schedules = scheduleRepository.findByUserAndScheduleDateBetweenAndDeletedAtNull(user, start, end);
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        schedules.forEach(schedule ->
                scheduleDtos.add(ScheduleDto.builder()
                        .scheduleId(schedule.getScheduleId())
                        .date(schedule.getScheduleDate())
                        .name(schedule.getName())
                        .cycle(schedule.getCycle())
                        .cycleEnd(schedule.getCycleEnd())
                        .isDone(schedule.getIsDone())
                        .petName(schedule.getPet().getName())
                        .petId(schedule.getPet().getPetId()).build())
        );

        return scheduleDtos;
    }

    // 일정 등록(생성)
    @Transactional
    public void createSchedule(Long userId, ScheduleCreateRequestDto request){
        // petId, userId 존재 검증
        Pet pet = petRepository.findById(request.getPetId()).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (request.getCycle().equals(ScheduleCycle.NONE)) {
            Schedule schedule = Schedule.builder()
                    .name(request.getName())
                    .scheduleDate(request.getDate())
                    .isDone(false)
                    .cycle(request.getCycle())
                    .cycleEnd(request.getCycleEnd())
                    .pet(pet)
                    .user(user).build();
            scheduleRepository.save(schedule);

        }else{
            for(LocalDate date = request.getDate(); date.isBefore(request.getCycleEnd()); date = date.plusDays(request.getCycle().getDays(date))){
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
    public void editSchedule(Long userId, ScheduleEditRequestDto request){
        // petId, userId 존재 여부 검증
        Pet pet = petRepository.findById(request.getPetId()).orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 일정 존재 여부 검증
        Schedule schedule = scheduleRepository.findById(request.getScheduleId()).orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        // 사이클 전체 or 해당 일정만 수정
        if (request.getCycleLink()){
            if (request.getCycle() == ScheduleCycle.NONE) {
                // 단일 일정만 수정
                schedule.setPet(pet);
                schedule.setName(request.getName());
                schedule.setScheduleDate(request.getDate());
                schedule.setCycle(request.getCycle());
                schedule.setCycleEnd(request.getCycleEnd());
                schedule.setUpdatedAt(OffsetDateTime.now());
                scheduleRepository.save(schedule);
                return;
            }
            List<Schedule> schedules = scheduleRepository.findByNameAndCycleAndCycleEnd(schedule.getName(), schedule.getCycle(), schedule.getCycleEnd());
            LocalDate date = request.getDate();
            for(Schedule schedule1: schedules){
                // 반복이 없으면 삭제 해당 일정 제외
                if (request.getCycle().equals(ScheduleCycle.NONE) && !schedule1.getScheduleId().equals(request.getScheduleId())){
                    schedule1.setDeletedAt(OffsetDateTime.now());
                    continue;
                }

                schedule.setPet(pet);
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
            // 부족한 일정 추가 생성
            for(;date.isBefore(request.getCycleEnd()); date = date.plusDays(request.getCycle().getDays(date))){
                Schedule addSchedule = Schedule.builder()
                        .name(request.getName())
                        .scheduleDate(date)
                        .cycle(request.getCycle())
                        .cycleEnd(request.getCycleEnd())
                        .isDone(false)
                        .pet(pet)
                        .user(user).build();
                schedules.add(addSchedule);
            }
            scheduleRepository.saveAll(schedules);
        }else {
            // 단독 일정을 반복일정으로 수정 시 추가 일정 생성
            if (!request.getCycle().equals(ScheduleCycle.NONE)) {
                List<Schedule> schedules = scheduleRepository.findByNameAndCycleAndCycleEnd(schedule.getName(), schedule.getCycle(), schedule.getCycleEnd());
                LocalDate date = request.getDate();
                for(;date.isBefore(request.getCycleEnd()); date = date.plusDays(request.getCycle().getDays(date))){
                    Schedule addSchedule = Schedule.builder()
                            .name(request.getName())
                            .scheduleDate(date)
                            .cycle(request.getCycle())
                            .cycleEnd(request.getCycleEnd())
                            .isDone(false)
                            .pet(pet)
                            .user(user).build();
                    schedules.add(addSchedule);
                }
                scheduleRepository.saveAll(schedules);
            }else{
                // 단독 일정만 수정
                schedule.setPet(pet);
                schedule.setName(request.getName());
                schedule.setScheduleDate(request.getDate());
                schedule.setUpdatedAt(OffsetDateTime.now());
                schedule.setCycle(request.getCycle());
                schedule.setCycleEnd(request.getCycleEnd());
                scheduleRepository.save(schedule);
            }
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

    public void checkIsDone(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
        if (!schedule.getUser().getUserId().equals(userId)) throw new UserException(UserErrorCode.USER_ACCESS_DENIED);

        schedule.setIsDone(!schedule.getIsDone());
        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteVaccinationSchedule(Pet pet, String keywords) {
        List<Schedule> schedules = scheduleRepository.findByPetAndNameContainingAndDeletedAtNull(pet, keywords);
        schedules.forEach(schedule -> schedule.setDeletedAt(OffsetDateTime.now()));
        scheduleRepository.saveAll(schedules);
    }
}
