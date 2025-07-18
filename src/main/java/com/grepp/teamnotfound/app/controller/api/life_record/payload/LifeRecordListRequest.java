package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LifeRecordListRequest {

    private Long petId;

    private LocalDate recordAt;

    @Min(value = 1, message = "페이지는 1 이상이어야 합니다.")
    private int page;

}
