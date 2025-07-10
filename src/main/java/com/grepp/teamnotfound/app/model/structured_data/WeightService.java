package com.grepp.teamnotfound.app.model.structured_data;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.WeightData;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.structured_data.dto.WeightDto;
import com.grepp.teamnotfound.app.model.structured_data.entity.Weight;
import com.grepp.teamnotfound.app.model.structured_data.repository.WeightRepository;
import com.grepp.teamnotfound.infra.error.exception.StructuredDataException;
import com.grepp.teamnotfound.infra.error.exception.code.NoteErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.SleepingErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.WeightErrorCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeightService {

    private final ModelMapper modelMapper;
    private final WeightRepository weightRepository;

    // 몸무게 정보 등록
    @Transactional
    public void createWeight(WeightDto weightDto){
        Weight weight = modelMapper.map(weightDto, Weight.class);
        weightRepository.save(weight);
    }

    // 몸무게 정보 조회
    @Transactional(readOnly = true)
    public WeightData getWeight(Pet pet, LocalDate recordedAt){
        Weight weight = weightRepository.findByPetAndRecordedAt(pet, recordedAt)
                .orElseThrow(() -> new StructuredDataException(WeightErrorCode.WEIGHT_NOT_FOUND));

        return WeightData.builder()
                .weightId(weight.getWeightId())
                .weight(weight.getWeight())
                .build();
    }

    // 몸무게 정보 수정
    @Transactional
    public void updateWeight(WeightData weightData){
        Weight weight = weightRepository.findByWeightId(weightData.getWeightId())
                .orElseThrow(() -> new StructuredDataException(WeightErrorCode.WEIGHT_NOT_FOUND));
        weight.setWeight(weightData.getWeight());
        weight.setUpdatedAt(OffsetDateTime.now());
        weightRepository.save(weight);
    }

    // 몸무게 정보 삭제
    @Transactional
    public void deleteWeight(Pet pet, LocalDate recordedAt){
        Weight weight = weightRepository.findByPetAndRecordedAt(pet, recordedAt)
                .orElseThrow(() -> new StructuredDataException(WeightErrorCode.WEIGHT_NOT_FOUND));
        weight.setDeletedAt(OffsetDateTime.now());
        weightRepository.save(weight);
    }

}
