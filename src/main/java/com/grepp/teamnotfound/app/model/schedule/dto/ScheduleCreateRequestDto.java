package com.grepp.teamnotfound.app.model.schedule.dto;

import com.grepp.teamnotfound.app.model.schedule.code.ScheduleCycle;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ScheduleCreateRequestDto {
    private Long userId;
    private Long petId;
    private String name;
    private LocalDate date;
    private ScheduleCycle cycle;
    private LocalDate cycleEnd;
}
