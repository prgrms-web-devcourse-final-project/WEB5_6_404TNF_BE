package com.grepp.teamnotfound.app.model.pet.repository;


import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.user.entity.User;
import feign.Param;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Pet findFirstByUser(User user);
    List<Pet> findAllByUser(User user);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("UPDATE Pet p SET p.deletedAt = :deletedAt WHERE p.petId = :petId")
    void softDelete(@Param("petId") Long petId, @Param("deletedAt") OffsetDateTime deletedAt);

}
