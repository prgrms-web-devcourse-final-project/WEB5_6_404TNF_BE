package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Builder
public class WalkingData {

    private Long walkingId;
    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;
    private Integer pace;
    private LocalDate recordedAt;

}
