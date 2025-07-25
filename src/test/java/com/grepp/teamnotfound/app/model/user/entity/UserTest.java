package com.grepp.teamnotfound.app.model.user.entity;

import com.grepp.teamnotfound.app.model.user.code.UserStateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    @DisplayName("deletedAt에 값이 있는 유저는 LEAVE 반환")
    void getUserState_DeletedUser_ReturnLeave() {
        // given
        User user = new User();
        user.setDeletedAt(OffsetDateTime.now());

        // when
        UserStateResponse state = user.getUserState();

        // then
        assertEquals(UserStateResponse.LEAVE, state);
    }


    @Test
    @DisplayName("suspendedAt에 오늘 이후의 값이 있으면 SUSPENSION 반환")
    void getUserState_SuspendedUser_ReturnSuspendedUser() {
        User user = User.builder()
                .suspensionEndAt(OffsetDateTime.now().plusDays(3))
                .build();

        // when
        UserStateResponse state = user.getUserState();

        //then
        assertEquals(UserStateResponse.SUSPENDED, state);
    }


    @Test
    @DisplayName("suspendedAt에 오늘 이전의 값이 있고, deletedAt이 null이면 ACTIVE 반환")
    void getUserState_ActiveUser_ReturnSuspendedUser() {
        User user = User.builder()
                .suspensionEndAt(OffsetDateTime.now().minusDays(3))
                .build();

        // when
        UserStateResponse state = user.getUserState();

        // then
        assertEquals(UserStateResponse.ACTIVE, state);
    }

    @Test
    @DisplayName("suspendedAt, deletedAt이 모두 null이면 ACTIVE 반환")
    void getUserState_ActiveUser_ReturnNullUser() {
        User user = new User();

        UserStateResponse state = user.getUserState();

        assertEquals(UserStateResponse.ACTIVE, state);
    }
}