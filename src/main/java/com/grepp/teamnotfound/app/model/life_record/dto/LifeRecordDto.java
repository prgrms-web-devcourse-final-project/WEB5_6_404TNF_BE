package com.grepp.teamnotfound.app.model.life_record.dto;

import com.grepp.teamnotfound.app.model.note.dto.NoteDto;
import com.grepp.teamnotfound.app.model.structured_data.dto.FeedingDto;
import com.grepp.teamnotfound.app.model.structured_data.dto.SleepingDto;
import com.grepp.teamnotfound.app.model.structured_data.dto.WalkingDto;
import com.grepp.teamnotfound.app.model.structured_data.dto.WeightDto;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;

@Data
public class LifeRecordDto {

    private Long petId;
    private LocalDate recordAt;

    // 관찰노트
    private NoteDto note;

    // 수면시간
    private SleepingDto sleepTime;
    // 몸무게
    private WeightDto weight;

    // 산책
    private List<WalkingDto> walkingList;

    // 식사량
    private List<FeedingDto> feedingList;

}
