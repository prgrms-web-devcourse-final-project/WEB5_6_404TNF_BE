package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Weight;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface WeightRepository extends JpaRepository<Weight, Long> {

    @Query("SELECT w FROM Weight w WHERE w.pet.petId = :petId AND w.recordedAt = :recordedAt AND w.deletedAt IS NULL")
    Optional<Weight> findWeight(Long petId, LocalDate recordedAt);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE Weight w SET w.deletedAt = CURRENT_TIMESTAMP WHERE w.pet.petId = :petId AND w.recordedAt = :recordedAt AND w.deletedAt IS NULL")
    void delete(Long petId, LocalDate recordedAt);

    Pet pet(Pet pet);
}
