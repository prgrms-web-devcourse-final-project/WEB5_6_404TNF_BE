package com.grepp.teamnotfound.app.model.note.dto;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDto {

    private Long noteId;

    @NotNull
    private String content;

    private LocalDate recordedAt;

    @NotNull
    private Pet pet;

    private OffsetDateTime createdAt = OffsetDateTime.now();
}
