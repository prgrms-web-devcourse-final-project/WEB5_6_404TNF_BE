package com.grepp.teamnotfound.app.model.life_record.repository;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordListRequest;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LifeRecordRepositoryCustom {
    Page<LifeRecordListDto> search(Long userId, LifeRecordListRequest request, Pageable pageable);
}
