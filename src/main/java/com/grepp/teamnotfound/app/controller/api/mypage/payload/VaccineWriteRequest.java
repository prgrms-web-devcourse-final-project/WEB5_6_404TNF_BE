package com.grepp.teamnotfound.app.controller.api.mypage.payload;

import com.grepp.teamnotfound.app.model.vaccination.code.VaccineName;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import java.time.LocalDate;
import lombok.Data;

@Data
public class VaccineWriteRequest {

    private VaccineName name;
    private LocalDate vaccineAt;
    private VaccineType vaccineType;
    private Integer count;

}
