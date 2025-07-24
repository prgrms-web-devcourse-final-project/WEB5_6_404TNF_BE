package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.model.user.dto.TotalUsersDto;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class UserCountResponse {

    private OffsetDateTime date;
    private long total;

    public static UserCountResponse from(TotalUsersDto dto){
        return UserCountResponse.builder()
                .date(OffsetDateTime.now())
                .total(dto.getTotal())
                .build();
    }
}
