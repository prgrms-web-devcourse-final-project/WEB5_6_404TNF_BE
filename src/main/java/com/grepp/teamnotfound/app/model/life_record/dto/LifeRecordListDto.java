package com.grepp.teamnotfound.app.model.life_record.dto;

import com.grepp.teamnotfound.app.model.life_record.entity.LifeRecord;
import java.time.Duration;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LifeRecordListDto {

    private Long lifeRecordId;
    private LifeRecordListPetDto pet;
    private LocalDate recordAt;
    private Double weight;
    private Integer walkingTime;
    private String content;

    public LifeRecordListDto(LifeRecord lifeRecord){
        this.lifeRecordId = lifeRecord.getLifeRecordId();
        this.pet = LifeRecordListPetDto.petInfoDto(lifeRecord.getPet().getPetImg());
        this.recordAt = lifeRecord.getRecordedAt();
        this.weight = lifeRecord.getWeight();
        // 생활기록의 총 산책시간 계산
        this.walkingTime = lifeRecord.getWalkingList().stream()
            .mapToInt(walking -> {
                Duration duration = Duration.between(walking.getStartTime(), walking.getEndTime());
                return (int) duration.toMinutes();
            }).sum();
        this.content = lifeRecord.getContent();
    }

}
