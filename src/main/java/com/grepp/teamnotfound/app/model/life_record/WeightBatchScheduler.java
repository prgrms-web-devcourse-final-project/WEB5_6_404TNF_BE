package com.grepp.teamnotfound.app.model.life_record;

import com.grepp.teamnotfound.app.model.life_record.entity.LifeRecord;
import com.grepp.teamnotfound.app.model.life_record.repository.LifeRecordRepository;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeightBatchScheduler {

    private final LifeRecordRepository lifeRecordRepository;

    @Scheduled(cron = "0 5 0 * * *") // 매일 00:05
    @Transactional
    public void updatePetWeightFromLifeRecords() {
        LocalDate today = LocalDate.now();
        List<LifeRecord> lifeRecords = lifeRecordRepository.findToday(today);

        for (LifeRecord lifeRecord : lifeRecords) {
            if (lifeRecord.getWeight() != null) {
                Pet pet = lifeRecord.getPet();
                pet.setWeight(lifeRecord.getWeight());
            }
        }

        log.info("펫 몸무게 {}건 업데이트 완료", lifeRecords.size());

    }
}
