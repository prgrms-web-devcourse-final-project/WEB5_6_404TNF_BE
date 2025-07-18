package com.grepp.teamnotfound.app.model.life_record.repository;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordListRequest;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordListDto;
import com.grepp.teamnotfound.app.model.life_record.entity.LifeRecord;
import com.grepp.teamnotfound.app.model.life_record.entity.QLifeRecord;
import com.grepp.teamnotfound.app.model.pet.entity.QPet;
import com.grepp.teamnotfound.app.model.pet.entity.QPetImg;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LifeRecordRepositoryImpl implements LifeRecordRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QLifeRecord lifeRecord = QLifeRecord.lifeRecord;
    QPet pet = QPet.pet;
    QPetImg petImg = QPetImg.petImg;

    @Override
    public Page<LifeRecordListDto> search(Long userId, LifeRecordListRequest request, Pageable pageable) {
        List<LifeRecord> content = queryFactory
            .selectFrom(lifeRecord)
            .where(
                lifeRecord.deletedAt.isNull(),
                lifeRecord.pet.user.userId.eq(userId),

                petIdEq(request.getPetId()),
                recordedAtEq(request.getRecordAt())
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(lifeRecord.recordedAt.desc())
            .fetch();

        if (content == null) return null;

        List<LifeRecordListDto> dtoList = content.stream()
                .map(LifeRecordListDto::new).toList();

        Long total = queryFactory
            .select(lifeRecord.count())
            .from(lifeRecord)
            .where(
                lifeRecord.deletedAt.isNull(),
                lifeRecord.pet.user.userId.eq(userId),

                petIdEq(request.getPetId()),
                recordedAtEq(request.getRecordAt())
            ).fetchOne();

        return new PageImpl<>(dtoList, pageable, total);
    }

    // petId를 필터링 할 경우
    private BooleanExpression petIdEq(Long petId) {
        return petId != null ? lifeRecord.pet.petId.eq(petId) : null;
    }

    // recordedAt을 필터링 할 경우
    private BooleanExpression recordedAtEq(LocalDate recordedAt) {
        return recordedAt != null ? lifeRecord.recordedAt.eq(recordedAt) : null;
    }
}
