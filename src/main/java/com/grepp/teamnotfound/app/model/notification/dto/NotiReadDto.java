package com.grepp.teamnotfound.app.model.notification.dto;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotiReadDto {

    NotiType type;
    Long targetId;

}
