package com.grepp.teamnotfound.app.model.user.repository;

import com.grepp.teamnotfound.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    User findByUserId(Long userId);
}
