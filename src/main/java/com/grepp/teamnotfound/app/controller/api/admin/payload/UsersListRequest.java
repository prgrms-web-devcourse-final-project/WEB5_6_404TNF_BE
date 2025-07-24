package com.grepp.teamnotfound.app.controller.api.admin.payload;

import com.grepp.teamnotfound.app.controller.api.admin.code.AdminListSortDirection;
import com.grepp.teamnotfound.app.controller.api.admin.code.UserStateFilter;
import com.grepp.teamnotfound.app.controller.api.admin.code.UsersListSortBy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UsersListRequest {

    @Min(value = 1, message="페이지는 1이상이어야 합니다.")
    private int page=1;
    @Min(value = 1, message="사이즈는 1이상이어야 합니다.")
    private int size=5;

    private String search;

    @Schema(description = "정렬 방법", example = "ASC | DESC", defaultValue = "ASC")
    private AdminListSortDirection sort = AdminListSortDirection.ASC;
    @Schema(description = "회원 목록 정렬 기준",
            example = "EMAIL | NICKNAME | POST_COUNT | COMMENT_COUNT | LAST_LOGIN_DATE | JOIN_DATE | STATE | SUSPENSION_END_DATE",
            defaultValue = "JOIN_DATE")
    private UsersListSortBy sortBy = UsersListSortBy.JOIN_DATE;
    @Schema(description = "회원 상태 필터", example = "ALL | ACTIVE | SUSPENDED | LEAVE", defaultValue = "ALL")
    private UserStateFilter status = UserStateFilter.ALL;
}
