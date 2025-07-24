package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.model.user.dto.UsersListDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class UsersListResponse {

    private List<UsersListDto> users;
    private PageInfo pageInfo;

    public static UsersListResponse from(Page<UsersListDto> userPage) {
        return UsersListResponse.builder()
                .users(userPage.getContent())
                .pageInfo(PageInfo.fromPage(userPage))
                .build();
    }
}
