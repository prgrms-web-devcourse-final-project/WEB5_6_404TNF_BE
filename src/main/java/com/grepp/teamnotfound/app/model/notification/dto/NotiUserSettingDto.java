package com.grepp.teamnotfound.app.model.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotiUserSettingDto {
    private Boolean isNotiAll;
    private Boolean isNotiSchedule;
    private Boolean isNotiService;
}
