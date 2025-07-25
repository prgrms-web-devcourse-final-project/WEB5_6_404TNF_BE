package com.grepp.teamnotfound.app.model.notification.repository;

import com.grepp.teamnotfound.app.model.notification.entity.NotiManagement;
import com.grepp.teamnotfound.app.model.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiManagementRepository extends JpaRepository<NotiManagement, Long> {

    Optional<NotiManagement> findByUser(User user);
}
