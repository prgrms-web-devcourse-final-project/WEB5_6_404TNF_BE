package com.grepp.teamnotfound.app.controller.api.profile.payload;

import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import lombok.Data;

@Data
public class ProfilePetResponse {
    private Long petId;
    private String registNumber;
    private String name;
    private Integer age;
    private Integer days;
    private PetType breed;
    private PetSize size;
    private Boolean sex;
    private Boolean isNeutered;
    private String imgUrl;
}
