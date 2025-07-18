package com.grepp.teamnotfound.app.model.pet.repository;

import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import feign.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface PetImgRepository extends JpaRepository<PetImg, Long> {

    Optional<PetImg> findByPet_PetIdAndDeletedAtIsNull(Long petId);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE PetImg pi SET pi.deletedAt = CURRENT_TIMESTAMP WHERE pi.pet.petId = :petId AND pi.deletedAt IS NULL")
    void softDeletePetImg(@Param("petId") Long petId);

}
