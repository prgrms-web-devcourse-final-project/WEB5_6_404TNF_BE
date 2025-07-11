package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grepp.teamnotfound.app.model.structured_data.FeedUnit;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Builder
public class FeedingData {

    private Double amount;
    private OffsetDateTime mealtime;
    private FeedUnit unit;

}
