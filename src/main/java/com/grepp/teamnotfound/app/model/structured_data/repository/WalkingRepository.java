package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Walking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WalkingRepository extends JpaRepository<Walking, Long> {

    List<Walking> findAllByPetAndRecordedAt(Pet pet, LocalDate recordedAt);

    Optional<Walking> findByWalkingId(Long walkingId);
}
