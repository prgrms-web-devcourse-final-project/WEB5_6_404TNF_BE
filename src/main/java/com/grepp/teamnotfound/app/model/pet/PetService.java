package com.grepp.teamnotfound.app.model.pet;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetWriteRequest;
import com.grepp.teamnotfound.app.controller.api.profile.payload.ProfilePetResponse;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.app.model.pet.repository.PetImgRepository;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
import com.grepp.teamnotfound.infra.code.ImgType;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.PetException;
import com.grepp.teamnotfound.infra.error.exception.code.CommonErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.PetErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import com.grepp.teamnotfound.infra.util.file.FileDto;
import com.grepp.teamnotfound.infra.util.file.GoogleStorageManager;
import com.grepp.teamnotfound.util.NotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final PetImgRepository petImgRepository;
    private final VaccinationService vaccinationService;
    private final GoogleStorageManager fileManager;

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
                dto.setDays((int) ChronoUnit.DAYS.between(pet.getMetday(), LocalDate.now()) + 1);
                dto.setAge(calculateAge(pet.getBirthday()));
                dto.setImgUrl(getProfileImgPath(pet.getPetId()));

                return dto;
            })
            .collect(Collectors.toList());
    }

    public PetDto findOne(Long petId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        PetDto dto = PetDto.fromEntity(pet);

        String imgUrl = getProfileImgPath(petId);
        dto.setImgUrl(imgUrl);

        return dto;
    }

    @Transactional
    public PetDto create(Long userId, PetWriteRequest request, List<MultipartFile> images) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        Pet pet = new Pet();

        modelMapper.map(request, pet);
        pet.setUser(user);

        petRepository.save(pet);
        uploadAndSaveImgs(images, pet);

        PetDto dto = PetDto.fromEntity(pet);
        dto.setImgUrl(getProfileImgPath(pet.getPetId()));

        return dto;
    }

    @Transactional
    public PetDto update(Long petId, PetWriteRequest request, List<MultipartFile> images) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new BusinessException(PetErrorCode.PET_NOT_FOUND));

        if (request.getMetday().isBefore(request.getBirthday())) {
            throw new BusinessException(PetErrorCode.PET_INVALID_DATES);
        }

        modelMapper.map(request, pet);
        pet.setUpdatedAt(OffsetDateTime.now());

        petRepository.save(pet);
        uploadAndSaveImgs(images, pet);

        PetDto dto = PetDto.fromEntity(pet);
        dto.setImgUrl(getProfileImgPath(pet.getPetId()));

        return dto;
    }

    @Transactional
    public void delete(Long petId) {
        Integer updated = petRepository.softDelete(petId);
        if (updated == 0) {
            throw new BusinessException(PetErrorCode.PET_NOT_FOUND);
        }

        vaccinationService.softDelete(petId);
        petImgRepository.softDeletePetImg(petId);

    }

    private Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        Period period = Period.between(birthday, LocalDate.now());
        return period.getYears() * 12 + period.getMonths();
    }

    public String getProfileImgPath(Long petId) {
        String profileImgPath = null;
        Optional<PetImg> optionalPetImg = petImgRepository.findByPet_PetIdAndDeletedAtIsNull(petId);
        if (optionalPetImg.isPresent()) {
            PetImg petImg = optionalPetImg.get();
            profileImgPath = petImg.getSavePath() + petImg.getRenamedName();
        }
        return profileImgPath;
    }

    private void uploadAndSaveImgs(List<MultipartFile> images, Pet pet) {
        // 기존 이미지 조회
        petImgRepository.softDeletePetImg(pet.getPetId());

        if (images == null || images.isEmpty()) {
            return;
        }

        try {
            // 버킷에 업로드
            FileDto fileDto = fileManager.upload(images, "pet")
                .stream()
                .findFirst()
                .orElseThrow(() -> new CommonException(CommonErrorCode.FILE_UPLOAD_FAILED));

            // 이미지 객체 생성 또는 수정
            PetImg petImg = new PetImg();
            petImg.setSavePath(fileDto.savePath());
            petImg.setOriginName(fileDto.originName());
            petImg.setRenamedName(fileDto.renamedName());
            petImg.setType(ImgType.THUMBNAIL);
            petImg.setPet(pet);

            petImgRepository.save(petImg);

        } catch (IOException e) {
            throw new CommonException(CommonErrorCode.FILE_UPLOAD_FAILED);
        }
    }
}
