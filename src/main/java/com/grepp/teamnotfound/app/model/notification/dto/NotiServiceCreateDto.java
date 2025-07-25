package com.grepp.teamnotfound.app.model.notification.dto;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotiServiceCreateDto implements NotiBasicDto{
    private Long targetId;
    private NotiType notiType;

    @Override
    public NotiType getType() {
        return this.notiType;
    }
}
