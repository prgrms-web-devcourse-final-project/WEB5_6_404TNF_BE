package com.grepp.teamnotfound.app.model.notification.dto;

import com.grepp.teamnotfound.app.model.notification.code.NotiType;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotiUserDto {

    private Long notiId;
    private NotiType type;
    private String content;
    private Long targetId;
    private Boolean isRead;
    private OffsetDateTime createdAt;

}
