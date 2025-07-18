package com.grepp.teamnotfound.app.controller.api.life_record.payload;

import com.grepp.teamnotfound.app.controller.api.article.payload.PageInfo;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordListDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LifeRecordListResponse {

    private List<LifeRecordListDto> data;
    private PageInfo pageInfo;

}
