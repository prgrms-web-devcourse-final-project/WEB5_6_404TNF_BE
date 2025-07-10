package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Sleeping;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepingRepository extends JpaRepository<Sleeping, Long> {

//    Sleeping findFirstByPet(Pet pet);

    Optional<Sleeping> findByPetAndRecordedAt(Pet pet, LocalDate recordedAt);

    Optional<Sleeping> findBySleepingId(Long sleepingId);
}
