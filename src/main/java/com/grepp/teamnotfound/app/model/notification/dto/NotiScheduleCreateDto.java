package com.grepp.teamnotfound.app.model.notification.dto;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotiScheduleCreateDto implements NotiBasicDto {
    private Long scheduleId;
    private LocalDate scheduleDate;

    @Override
    public NotiType getType() {
        return NotiType.SCHEDULE;
    }
}

