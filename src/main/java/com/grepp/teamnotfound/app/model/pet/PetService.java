package com.grepp.teamnotfound.app.model.pet;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetWriteRequest;
import com.grepp.teamnotfound.app.controller.api.profile.payload.ProfilePetResponse;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccinationRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.PetException;
import com.grepp.teamnotfound.infra.error.exception.code.PetErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import com.grepp.teamnotfound.util.NotFoundException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
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

    @Transactional
    public Pet getPet(Long petId){
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
    }

    public List<PetDto> findAll() {
        List<Pet> pets = petRepository.findAll();

        return pets.stream()
            .map(PetDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ProfilePetResponse> findByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Pet> pets = petRepository.findAllByUser(user);

        return pets.stream()
            .map(pet -> {
                ProfilePetResponse dto = modelMapper.map(pet, ProfilePetResponse.class);

                dto.setAge(calculateAge(pet.getBirthday()));

                if (pet.getMetday() != null) {
                    dto.setDays((int) ChronoUnit.DAYS.between(pet.getMetday(), LocalDate.now()) + 1);
                } else {
                    dto.setDays(null);
                }

                return dto;
            })
            .collect(Collectors.toList());
    }

    public PetDto findOne(Long petId) {
        petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        return petRepository.findById(petId)
                .map(PetDto::fromEntity)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(PetWriteRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        Pet pet = new Pet();

        modelMapper.getConfiguration().setPropertyCondition(ctx -> !ctx.getMapping().getLastDestinationProperty().getName().equals("petId"));
        modelMapper.map(request, pet);
        pet.setAge(calculateAge(request.getBirthday()));
        pet.setUser(user);

        petRepository.save(pet);

        return pet.getPetId();
    }

    @Transactional
    public Long update(Long petId, PetWriteRequest request) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        modelMapper.map(request, pet);
        pet.setAge(calculateAge(request.getBirthday()));
        pet.setUpdatedAt(OffsetDateTime.now());

        petRepository.save(pet);

        return pet.getPetId();
    }

    @Transactional
    public void delete(Long petId) {
        petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

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
