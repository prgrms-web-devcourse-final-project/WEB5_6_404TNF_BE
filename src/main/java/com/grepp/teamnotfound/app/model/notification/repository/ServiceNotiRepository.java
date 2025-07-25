package com.grepp.teamnotfound.app.model.notification.repository;

import com.grepp.teamnotfound.app.model.notification.entity.ServiceNoti;
import feign.Param;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ServiceNotiRepository extends JpaRepository<ServiceNoti, Long> {

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE ServiceNoti s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.user.userId = :userId")
    void deleteAllByUser(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ServiceNoti s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.serviceNotiId = :notiId")
    void deleteOne(@Param("notiId") Long notiId);

    @Query("SELECT s FROM ServiceNoti s WHERE s.deletedAt IS NULL AND s.user.userId = :userId AND s.createdAt >= :monthBefore")
    List<ServiceNoti> getAllNoti(@Param("userId") Long userId, @Param("monthBefore") OffsetDateTime monthBefore);
}
