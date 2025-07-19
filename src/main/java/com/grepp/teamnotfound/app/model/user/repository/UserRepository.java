package com.grepp.teamnotfound.app.model.user.repository;

import com.grepp.teamnotfound.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByUserId(Long userId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    int countJoinedUsersBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt BETWEEN :start AND :end")
    int countLeftUsersBetween(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    @Query("SELECT u.nickname FROM User u where u.userId = :userid")
    String findNicknameByUserId(Long userid);

    boolean existsByNickname(String nickname);
}
