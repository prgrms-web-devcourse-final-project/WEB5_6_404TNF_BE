package com.grepp.teamnotfound.app.controller.api.mypage.payload;

import com.grepp.teamnotfound.app.model.vaccination.code.VaccineName;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VaccineWriteRequest {

    private VaccineName name;
    private LocalDate vaccineAt;
    private VaccineType vaccineType;
    private Integer count;

}
