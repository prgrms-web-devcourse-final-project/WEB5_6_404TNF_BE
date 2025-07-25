package com.grepp.teamnotfound.app.model.user.repository;

import com.grepp.teamnotfound.app.model.user.entity.UserImg;
import feign.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserImgRepository extends JpaRepository<UserImg, Long> {

    Optional<UserImg> findByUser_UserIdAndDeletedAtIsNull(Long userId);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE UserImg ui SET ui.deletedAt = CURRENT_TIMESTAMP WHERE ui.user.userId = :userId AND ui.deletedAt IS NULL")
    void softDeleteUserImg(@Param("userId") Long userId);

    List<UserImg> findByDeletedAtBetween(OffsetDateTime startOfTargetDay, OffsetDateTime endOfTargetDay);
}
