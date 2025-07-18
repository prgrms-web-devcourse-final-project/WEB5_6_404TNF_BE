package com.grepp.teamnotfound.app.model.vaccination;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.VaccineWriteRequest;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineName;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest
class VaccinationServiceTest {

    @Autowired
    private VaccinationService vaccinationService;
    @Autowired
    private PetService petService;

    @Test
    void testSavePetVaccination(){

        vaccinationService.savePetVaccinations(10001L, List.of(VaccineWriteRequest.builder()
                .vaccineAt(LocalDate.now())
                .vaccineType(VaccineType.BOOSTER)
                .count(3).name(VaccineName.DHPPL).build(),
                VaccineWriteRequest.builder()
                        .vaccineType(VaccineType.ADDITIONAL)
                        .name(VaccineName.CORONAVIRUS)
                        .vaccineAt(LocalDate.now())
                        .count(3).build(),
                VaccineWriteRequest.builder()
                        .vaccineType(VaccineType.BOOSTER)
                        .name(VaccineName.KENNEL_COUGH)
                        .vaccineAt(LocalDate.now())
                        .count(2).build()
                ));
        vaccinationService.createVaccinationSchedule(10001L, 10000L);

    }


}