package com.grepp.teamnotfound.app.model.vaccination.repository;


import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccination;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import feign.Param;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {

    Vaccination findFirstByVaccine(Vaccine vaccine);

    Vaccination findFirstByPet(Pet pet);


    List<Vaccination> findAllByPetEquals(Pet pet);

    @Modifying
    @Query("UPDATE Vaccination v SET v.deletedAt = :deletedAt WHERE v.pet.petId = :petId")
    void softDelete(@Param("petId") Long petId, @Param("deletedAt") OffsetDateTime deletedAt);
}
