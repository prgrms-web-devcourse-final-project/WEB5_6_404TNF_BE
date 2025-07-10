package com.grepp.teamnotfound.app.model.pet.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PetImgRepository extends JpaRepository<PetImg, Long> {

    PetImg findFirstByPet(Pet pet);

    boolean existsByPetPetId(Long petId);

}
