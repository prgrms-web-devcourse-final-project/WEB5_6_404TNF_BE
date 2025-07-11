package com.grepp.teamnotfound.app.model.note.dto;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoteDto {

    private Long noteId;
    private Long petId;

    private String content;
    private LocalDate recordedAt;

    private OffsetDateTime createdAt = OffsetDateTime.now();
}
