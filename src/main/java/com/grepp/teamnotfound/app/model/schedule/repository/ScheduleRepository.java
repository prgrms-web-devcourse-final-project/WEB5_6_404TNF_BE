package com.grepp.teamnotfound.app.model.schedule.repository;

import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.schedule.code.ScheduleCycle;
import com.grepp.teamnotfound.app.model.schedule.entity.Schedule;
import com.grepp.teamnotfound.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByNameAndCycleAndCycleEnd(String name, ScheduleCycle cycle, LocalDate cycleEnd);

    List<Schedule> findByUserAndScheduleDateBetweenAndDeletedAtNull(User user, LocalDate start, LocalDate end);

    List<Schedule> findByPetAndNameContainingAndDeletedAtNull(Pet pet, String name);
}
