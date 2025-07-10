package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Builder
public class NoteData {

    private Long noteId;
    private String content;

}
