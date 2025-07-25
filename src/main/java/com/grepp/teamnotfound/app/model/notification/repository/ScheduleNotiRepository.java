package com.grepp.teamnotfound.app.model.notification.repository;

import com.grepp.teamnotfound.app.model.notification.entity.ScheduleNoti;
import feign.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleNotiRepository extends JpaRepository<ScheduleNoti, Long> {

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE ScheduleNoti s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.user.userId = :userId")
    void deleteAllByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ScheduleNoti s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.scheduleNotiId = :notiId")
    void deleteOne(@Param("notiId") Long notiId);

    @Query("SELECT s FROM ScheduleNoti s " +
        "WHERE s.deletedAt IS NULL " +
        "AND s.user.userId = :userId " +
        "AND s.notiDate BETWEEN :startMonth AND :tomorrow")
    List<ScheduleNoti> getAllNoti(@Param("userId") Long userId, @Param("startMonth") LocalDate startMonth, @Param("today") LocalDate tomorrow);

    @Query("select case when count(s) > 0 then true else false end from ScheduleNoti s where s.schedule.scheduleId = :scheduleId")
    boolean existsByScheduleId(@Param("scheduleId") Long scheduleId);
}
