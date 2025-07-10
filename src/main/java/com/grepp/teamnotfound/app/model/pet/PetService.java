package com.grepp.teamnotfound.app.model.pet;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetCreateRequest;
import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetEditRequest;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccinationDto;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccinationRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.PetErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import com.grepp.teamnotfound.util.NotFoundException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final VaccinationRepository vaccinationRepository;

    private final VaccinationService vaccinationService;

    ModelMapper modelMapper = new ModelMapper();

    public List<PetDto> findAll() {
        List<Pet> pets = petRepository.findAll();

        return pets.stream()
            .map(pet -> {
                List<VaccinationDto> vaccinations = vaccinationRepository.findAllByPetEquals(pet).stream()
                    .map(VaccinationDto::fromEntity)
                    .toList();
                return PetDto.fromEntity(pet, vaccinations);
            })
            .toList();
    }

    public List<PetDto> findByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Pet> pets = petRepository.findAllByUser(user);

        return pets.stream()
            .map(pet -> {
                List<VaccinationDto> vaccinations = vaccinationRepository.findAllByPetEquals(pet).stream()
                    .map(VaccinationDto::fromEntity)
                    .toList();
                return PetDto.fromEntity(pet, vaccinations);
            })
            .toList();
    }

    public PetDto findOne(Long petId) {
        return petRepository.findById(petId)
                .map(pet -> {
                    List<VaccinationDto> vaccinations = vaccinationRepository.findAllByPetEquals(pet).stream()
                        .map(VaccinationDto::fromEntity)
                        .toList();
                    return PetDto.fromEntity(pet, vaccinations);
                })
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(PetCreateRequest request) {
        User user = userRepository.findById(request.getUser())
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        Pet pet = modelMapper.map(request, Pet.class);
        pet.setAge(calculateAge(request.getBirthday()));
        pet.setUser(user);

        petRepository.save(pet);

        vaccinationService.savePetVaccinations(pet, request.getVaccinations());

        return pet.getPetId();
    }

    @Transactional
    public Long update(Long petId, PetEditRequest request) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        modelMapper.map(request, pet);
        pet.setAge(calculateAge(request.getBirthday()));
        pet.setUpdatedAt(OffsetDateTime.now());

        vaccinationService.softDelete(petId);
        vaccinationService.savePetVaccinations(pet, request.getVaccinations());

        return pet.getPetId();
    }

    @Transactional
    public void delete(Long petId) {
        petRepository.softDelete(petId, OffsetDateTime.now());
        vaccinationService.softDelete(petId);
    }

    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        Period period = Period.between(birthday, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }
}
