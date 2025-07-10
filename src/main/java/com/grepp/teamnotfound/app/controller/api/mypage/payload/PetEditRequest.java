package com.grepp.teamnotfound.app.controller.api.mypage.payload;

import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import com.grepp.teamnotfound.app.model.pet.dto.PetImgDto;
import com.grepp.teamnotfound.app.model.vaccination.code.VaccineType;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class PetEditRequest {

    private String registNumber;
    private LocalDate birthday;
    private LocalDate metday;
    private String name;
    private PetType breed;
    private PetSize size;
    private Double weight;
    private Boolean sex;
    private Boolean isNeutered;
    private PetImgDto image;
    private List<Vaccinated> vaccinations;

    @Data
    public static class Vaccinated implements VaccinatedItem{
        private Long vaccineId;
        private LocalDate vaccineAt;
        private VaccineType vaccineType;
        private Integer count;
        private Boolean isVaccine;
    }
}
