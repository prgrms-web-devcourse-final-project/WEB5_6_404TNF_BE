package com.grepp.teamnotfound.app.model.structured_data.dto;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalkingDto {

    private Long walkingId;
    private Long petId;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer pace;
    private LocalDate recordedAt;

    private OffsetDateTime createdAt = OffsetDateTime.now();

}
