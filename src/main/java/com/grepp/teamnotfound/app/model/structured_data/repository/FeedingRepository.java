package com.grepp.teamnotfound.app.model.structured_data.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.entity.Feeding;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface FeedingRepository extends JpaRepository<Feeding, Long> {

    @Query("SELECT f FROM Feeding f WHERE f.pet.petId = :petId AND f.recordedAt = :recordedAt AND f.deletedAt IS NULL")
    List<Feeding> findFeedingList(Long petId, LocalDate recordedAt);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE Feeding f SET f.deletedAt = CURRENT_TIMESTAMP WHERE f.pet.petId = :petId AND f.recordedAt = :recordedAt AND f.deletedAt IS NULL")
    void delete(Long petId, LocalDate recordedAt);

}
