package com.grepp.teamnotfound.app.model.structured_data;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.WalkingData;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.dto.WalkingDto;
import com.grepp.teamnotfound.app.model.structured_data.entity.Walking;
import com.grepp.teamnotfound.app.model.structured_data.repository.WalkingRepository;
import com.grepp.teamnotfound.infra.error.exception.StructuredDataException;
import com.grepp.teamnotfound.infra.error.exception.code.WalkingErrorCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkingService {

    private final ModelMapper modelMapper;
    private final WalkingRepository walkingRepository;

    // 산책 정보 생성
    @Transactional
    public void createWalking(WalkingDto walkingDto){
        Walking walking = modelMapper.map(walkingDto, Walking.class);
        walkingRepository.save(walking);
    }

    // 산책 정보 리스트 조회
    @Transactional(readOnly = true)
    public List<WalkingData> getWalkingList(Pet pet, LocalDate recordedAt){
        List<Walking> walkingList = walkingRepository.findAllByPetAndRecordedAt(pet, recordedAt);

        if(walkingList.isEmpty()) return List.of();

        return walkingList.stream().map(walking ->
            WalkingData.builder()
                .walkingId(walking.getWalkingId())
                .startedAt(walking.getStartedAt())
                .endedAt(walking.getEndedAt())
                .pace(walking.getPace())
                .build()).collect(Collectors.toList());
    }

    // 산책 정보 수정
    @Transactional
    public void updateWalkingList(List<WalkingData> walkingDataList){
        for(WalkingData walkingData : walkingDataList){
            Walking walking = walkingRepository.findByWalkingId(walkingData.getWalkingId())
                    .orElseThrow(() -> new StructuredDataException(WalkingErrorCode.WALKING_NOT_FOUND));
            
            walking.setStartedAt(walkingData.getStartedAt());
            walking.setEndedAt(walkingData.getEndedAt());
            walking.setPace(walkingData.getPace());
            walking.setUpdatedAt(OffsetDateTime.now());
            walkingRepository.save(walking);
        }
    }

    // 산책 정보 삭제
    @Transactional
    public void deleteWalkingList(Pet pet, LocalDate recordedAt){
        List<Walking> walkingList = walkingRepository.findAllByPetAndRecordedAt(pet, recordedAt);

        if (walkingList.isEmpty()) return;

        walkingList.forEach(walking -> {
            walking.setDeletedAt(OffsetDateTime.now());
            walkingRepository.save(walking);
        });
    }

}
