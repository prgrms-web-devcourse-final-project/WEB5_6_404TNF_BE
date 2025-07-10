package com.grepp.teamnotfound.app.model.structured_data;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.SleepingData;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.dto.SleepingDto;
import com.grepp.teamnotfound.app.model.structured_data.entity.Sleeping;
import com.grepp.teamnotfound.app.model.structured_data.repository.SleepingRepository;
import com.grepp.teamnotfound.infra.error.exception.StructuredDataException;
import com.grepp.teamnotfound.infra.error.exception.code.SleepingErrorCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SleepingService {

    private final ModelMapper modelMapper;
    private final SleepingRepository sleepingRepository;

    // 수면 정보 등록
    @Transactional
    public void createSleeping(SleepingDto sleepingDto){
        Sleeping sleeping = modelMapper.map(sleepingDto, Sleeping.class);
        sleepingRepository.save(sleeping);
    }

    // 수면 정보 조회
    @Transactional(readOnly = true)
    public SleepingData getSleeping(Pet pet, LocalDate recordedAt){
        Sleeping sleeping = sleepingRepository.findByPetAndRecordedAt(pet, recordedAt)
                .orElseThrow(() -> new StructuredDataException(SleepingErrorCode.SLEEPING_NOT_FOUND));

        return SleepingData.builder()
                .sleepingId(sleeping.getSleepingId())
                .sleepTime(sleeping.getSleepingTime())
                .build();
    }

    // 수면 정보 수정
    @Transactional
    public void updateSleeping(SleepingData sleepingData){
        Sleeping sleeping = sleepingRepository.findBySleepingId(sleepingData.getSleepingId())
                .orElseThrow(() -> new StructuredDataException(SleepingErrorCode.SLEEPING_NOT_FOUND));
        sleeping.setSleepingTime(sleepingData.getSleepTime());
        sleeping.setUpdatedAt(OffsetDateTime.now());
        sleepingRepository.save(sleeping);
    }

    // 수면 정보 삭제
    public void deleteSleeping(Pet pet, LocalDate recordedAt) {
        Sleeping sleeping = sleepingRepository.findByPetAndRecordedAt(pet, recordedAt)
                .orElseThrow(() -> new StructuredDataException(SleepingErrorCode.SLEEPING_NOT_FOUND));
        sleeping.setDeletedAt(OffsetDateTime.now());
        sleepingRepository.save(sleeping);
    }

}
