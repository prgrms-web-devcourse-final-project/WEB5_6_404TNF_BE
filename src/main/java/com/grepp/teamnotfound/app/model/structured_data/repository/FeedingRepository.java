package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Feeding;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FeedingRepository extends JpaRepository<Feeding, Long> {

    List<Feeding> findAllByPetAndRecordedAt(Pet pet, LocalDate recordedAt);

    Optional<Feeding> findByFeedingId(Long feedingId);
}
