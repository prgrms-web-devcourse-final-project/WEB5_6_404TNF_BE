package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LifeRecordListResponse {

    private Long noteId;
    private String name;
    private String savePath;
    private Float weight;
    private String content;
    private OffsetDateTime createdAt;

}
