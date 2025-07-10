package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class LifeRecordData {

    private Long petId;
    private LocalDate recordAt;

    // 관찰노트
    private NoteData note;

    // 수면시간
    private SleepingData sleepTime;
    // 몸무게
    private WeightData weight;

    // 산책
    private List<WalkingData> walkingList;

    // 식사량
    private List<FeedingData> feedingList;

}
