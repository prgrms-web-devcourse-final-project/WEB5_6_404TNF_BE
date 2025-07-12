package com.grepp.teamnotfound.app.controller.api.mypage.payload;

import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import com.grepp.teamnotfound.app.model.pet.dto.PetImgDto;
import java.time.LocalDate;
import lombok.Data;

@Data
public class PetWriteRequest {
    private String registNumber;
    private LocalDate birthday;
    private LocalDate metday;
    private String name;
    private PetType breed;
    private PetSize size;
    private Double weight;
    private Boolean sex;
    private Boolean isNeutered;
    private Long userId;
    private PetImgDto image;
}


