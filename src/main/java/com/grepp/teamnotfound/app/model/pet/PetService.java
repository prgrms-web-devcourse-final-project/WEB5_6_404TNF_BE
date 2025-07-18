package com.grepp.teamnotfound.app.model.pet;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetWriteRequest;
import com.grepp.teamnotfound.app.controller.api.profile.payload.ProfilePetResponse;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
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
import java.util.Map;
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

    private final VaccinationService vaccinationService;

    ModelMapper modelMapper = new ModelMapper();

    @Transactional
    public Pet getPet(Long petId){
        return petRepository.findById(petId)
                .orElseThrow(() -> new PetException(PetErrorCode.PET_NOT_FOUND));
    }

    public List<PetDto> findAll() {
        List<Pet> pets = petRepository.findAll();

        if (pets.isEmpty()) {
            throw new BusinessException(PetErrorCode.PET_NOT_FOUND);
        }

        return pets.stream()
            .map(PetDto::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ProfilePetResponse> findByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        List<Pet> pets = petRepository.findAllByUser(user.getUserId());

        return pets.stream()
            .map(pet -> {
                ProfilePetResponse dto = modelMapper.map(pet, ProfilePetResponse.class);
                dto.setPetId(pet.getPetId());
                dto.setDays((int) ChronoUnit.DAYS.between(pet.getMetday(), LocalDate.now()) + 1);
                dto.setAge(calculateAge(pet.getBirthday()));

                return dto;
            })
            .collect(Collectors.toList());
    }

    public PetDto findOne(Long petId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        return PetDto.fromEntity(pet);
    }

    @Transactional
    public Pet create(Long userId, PetWriteRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        Pet pet = new Pet();

        modelMapper.map(request, pet);
        pet.setUser(user);

        petRepository.save(pet);

        return pet;
    }

    @Transactional
    public PetDto update(Long petId, PetWriteRequest request) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        modelMapper.map(request, pet);
        pet.setUpdatedAt(OffsetDateTime.now());
        pet.setUser(pet.getUser());

        petRepository.save(pet);

        return PetDto.fromEntity(pet);
    }

    @Transactional
    public void delete(Long petId) {
        Integer updated = petRepository.softDelete(petId, OffsetDateTime.now());
        if (updated == 0) {
            throw new BusinessException(PetErrorCode.PET_NOT_FOUND);
        }

        vaccinationService.softDelete(petId);
    }

    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        Period period = Period.between(birthday, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    // 유저의 <petId, name> 리스트 조회
    @Transactional(readOnly = true)
    public List<Map<Long, String>> findPetListByUserId(Long userId){
        List<Pet> petList = petRepository.findPetIdsByUserId(userId);

        return petList.stream().map(pet ->
                Map.of(pet.getPetId(), pet.getName())).toList();
    }
}
