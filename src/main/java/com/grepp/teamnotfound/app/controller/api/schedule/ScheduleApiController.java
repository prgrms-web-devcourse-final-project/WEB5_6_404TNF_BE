package com.grepp.teamnotfound.app.controller.api.schedule;

import com.grepp.teamnotfound.app.controller.api.schedule.payload.ScheduleCreateRequest;
import com.grepp.teamnotfound.app.controller.api.schedule.payload.ScheduleEditRequest;
import com.grepp.teamnotfound.app.model.schedule.ScheduleService;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleCreateRequestDto;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleDto;
import com.grepp.teamnotfound.app.model.schedule.dto.ScheduleEditRequestDto;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class ScheduleApiController {

    private ScheduleService scheduleService;
    ModelMapper modelMapper = new ModelMapper();

    // 일정 조회 시 한달치의 일정 넘기기
    @GetMapping("/{petId}/calendar")
    public ResponseEntity<?> getPetCalendar(
            @PathVariable Long petId,
            @RequestParam Long userId,
            @RequestParam LocalDate date
            ){
        //todo 요청한 유저가 맞는지 검증로직

        List<Schedule> schedules = scheduleService.getCalendar(petId, date);
        List<ScheduleDto> scheduleDtos = new ArrayList<>();
        schedules.forEach(schedule ->
            scheduleDtos.add(ScheduleDto.builder()
                            .scheduleId(schedule.getScheduleId())
                            .date(schedule.getScheduleDate())
                            .name(schedule.getName())
                            .isDone(schedule.getIsDone()).build())
        );
        System.out.println(scheduleDtos);
        return ResponseEntity.ok(Map.of("data", scheduleDtos));
    }

    @PostMapping("{petId}/calendar")
    public ResponseEntity<?> createSchedule(
            @PathVariable Long petId,
            @RequestBody ScheduleCreateRequest request
    ){
        ScheduleCreateRequestDto requestDto = modelMapper.map(request, ScheduleCreateRequestDto.class);;
        scheduleService.createSchedule(requestDto);

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PatchMapping("{petId}/calendar")
    public ResponseEntity<?> EditSchedule(
            @PathVariable Long petId,
            @RequestBody ScheduleEditRequest request
    ){
        ScheduleEditRequestDto requestDto = modelMapper.map(request, ScheduleEditRequestDto.class);;
        scheduleService.editSchedule(requestDto);

        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PatchMapping("{petId}/calendar/delete")
    public ResponseEntity<?> DeleteSchedule(
            @PathVariable Long petId,
            @RequestParam Long userId,
            @RequestParam Long scheduleId,
            @RequestParam Boolean cycleLink
    ){
        scheduleService.deleteSchedule(userId, scheduleId, cycleLink);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("{petId}/calendar/{scheduleId}")
    public ResponseEntity<?> ScheduleIsDone(
            @PathVariable Long petId,
            @PathVariable Long scheduleId
    ){
        scheduleService.checkIsDone(petId, scheduleId);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}