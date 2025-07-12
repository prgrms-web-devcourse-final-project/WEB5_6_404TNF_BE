package com.grepp.teamnotfound.app.model.pet;

import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccinationRepository;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccineRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PetServiceTest {

    @Autowired
    private PetService petService;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private VaccineRepository vaccineRepository;
    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Test
    void testFindAll() {
        List<PetDto> result = petService.findAll();

        result.forEach(p -> System.out.println("PetDTO" + p));
    }

    @Test
    void testFindOne() {
        PetDto result = petService.findOne(1L);

        System.out.println("PetDTO" + result);
    }

}