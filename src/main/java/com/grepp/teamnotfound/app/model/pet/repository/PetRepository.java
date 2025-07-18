package com.grepp.teamnotfound.app.model.pet.repository;


import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import feign.Param;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p FROM Pet p WHERE p.user.userId = :userId AND p.deletedAt IS NULL ORDER BY p.petId")
    List<Pet> findAllByUser(Long userId);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE Pet p SET p.deletedAt = :deletedAt WHERE p.petId = :petId")
    Integer softDelete(@Param("petId") Long petId, @Param("deletedAt") OffsetDateTime deletedAt);

    @Query("SELECT p FROM Pet p WHERE p.user.userId = :userId AND p.deletedAt IS NULL")
    List<Pet> findPetIdsByUserId(@Param("userId") Long userId);

}
