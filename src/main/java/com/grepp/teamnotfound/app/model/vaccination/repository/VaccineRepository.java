package com.grepp.teamnotfound.app.model.vaccination.repository;


import com.grepp.teamnotfound.app.model.vaccination.code.VaccineName;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import feign.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

    @Query("SELECT v FROM Vaccine v WHERE v.name = :name")
    Optional<Vaccine> findByName(@Param("name") VaccineName name);

}
