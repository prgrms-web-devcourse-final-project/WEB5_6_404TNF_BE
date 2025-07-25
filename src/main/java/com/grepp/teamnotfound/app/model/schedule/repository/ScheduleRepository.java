package com.grepp.teamnotfound.app.model.schedule.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.schedule.code.ScheduleCycle;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import com.grepp.teamnotfound.app.model.user.entity.User;
import feign.Param;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByNameAndCycleAndCycleEnd(String name, ScheduleCycle cycle, LocalDate cycleEnd);

    List<Schedule> findByUserAndScheduleDateBetweenAndDeletedAtNull(User user, LocalDate start, LocalDate end);

    List<Schedule> findByPetAndNameContainingAndDeletedAtNull(Pet pet, String name);

    @Query("SELECT s FROM Schedule s WHERE s.pet.petId = :petId AND s.scheduleDate = :date AND s.deletedAt IS NULL")
    List<Schedule> findChecklist(@Param("petId") Long petId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate = :tomorrow AND s.isDone = false AND s.deletedAt IS NULL")
    List<Schedule> findSchedulesForNotification(LocalDate tomorrow);
}
