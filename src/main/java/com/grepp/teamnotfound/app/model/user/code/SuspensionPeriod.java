package com.grepp.teamnotfound.app.model.user.code;


import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Arrays;

@Getter
public enum SuspensionPeriod {

    ONE_DAY(1),
    TWO_DAYS(2),
    THREE_DAYS(3),
    FIVE_DAYS(5),
    SEVEN_DAYS(7),
    FOURTEEN_DAYS(14),
    THIRTY_DAYS(30),
    THREE_MONTHS(31),
    ONE_YEAR(365),
    PERMANENT(-1);      // 영구 정지

    private final int days;

    SuspensionPeriod(int days) {
        this.days = days;
    }

    public boolean isPermanent() {
        return this == PERMANENT;
    }

    public OffsetDateTime calculateEndTime(OffsetDateTime from) {
        return isPermanent() ? null : from.plusDays(days);
    }

    public static SuspensionPeriod fromDays(int days) {
        return Arrays.stream(values())
                .filter(p -> p.days == days)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 제재 기간입니다: " + days + "일"));
    }


}
