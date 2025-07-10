package com.grepp.teamnotfound.app.model.vaccination.repository;


import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
}
