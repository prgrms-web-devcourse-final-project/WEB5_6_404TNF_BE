package com.grepp.teamnotfound.app.controller.api.mypage.payload;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import java.time.LocalDate;

public interface VaccinatedItem {
    Long getVaccineId();
    LocalDate getVaccineAt();
    VaccineType getVaccineType();
    Integer getCount();
    Boolean getIsVaccine();
}