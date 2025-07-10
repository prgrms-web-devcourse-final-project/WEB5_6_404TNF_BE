package com.grepp.teamnotfound.app.model.vaccination.dto;

import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccination;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationDto {

    private Long pet;
    private Long vaccinationId;
    private LocalDate vaccineAt;
    private VaccineType vaccineType;
    private Integer count;
    private Boolean isVaccine;
    private Vaccine vaccine;

    public static VaccinationDto fromEntity(Vaccination vaccination) {
        if (vaccination == null) {
            return null;
        }

        return new VaccinationDto(
            vaccination.getPet() != null ? vaccination.getPet().getPetId() : null,
            vaccination.getVaccinationId(),
            vaccination.getVaccineAt(),
            vaccination.getVaccineType(),
            vaccination.getCount(),
            vaccination.getIsVaccine(),
            vaccination.getVaccine()
        );
    }
}
