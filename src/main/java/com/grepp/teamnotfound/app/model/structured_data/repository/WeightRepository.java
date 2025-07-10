package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Weight;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WeightRepository extends JpaRepository<Weight, Long> {

    Optional<Weight> findByPetAndRecordedAt(Pet pet, LocalDate recordedAt);

    Optional<Weight> findByWeightId(Long weightId);
}
