package com.grepp.teamnotfound.app.model.structured_data.dto;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeightDto {

    private Long weightId;

    @NotNull
    private Double weight;

    @NotNull
    private LocalDate recordedAt;

    @NotNull
    private Pet pet;

    private OffsetDateTime createdAt = OffsetDateTime.now();

}
