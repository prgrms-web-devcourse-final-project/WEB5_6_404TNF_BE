package com.grepp.teamnotfound.app.model.pet.scheduler;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetScheduler {

    private final PetRepository petRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void updatePetAges() {
        List<Pet> pets = petRepository.findAll();

        for (Pet pet : pets) {
            if (shouldIncreaseAge(pet)) {
                pet.setAge(pet.getAge() + 1);
            }
        }

        petRepository.saveAll(pets);
    }

    private boolean shouldIncreaseAge(Pet pet) {
        LocalDate birthday = pet.getBirthday();
        LocalDate now = LocalDate.now();
        LocalDate ageDate = birthday.plusMonths(pet.getAge() + 1);

        return !now.isBefore(ageDate);
    }
}

