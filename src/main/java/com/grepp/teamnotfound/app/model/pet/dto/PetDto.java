package com.grepp.teamnotfound.app.model.pet.dto;

import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.app.model.user.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {

    private Long petId;
    private String registNumber;
    private LocalDate birthday;
    private LocalDate metday;
    private String name;
    private Integer age;
    private PetType breed;
    private PetSize size;
    private Double weight;
    private Boolean sex;
    private Boolean isNeutered;
    private Long user;
    private PetImgDto image;

    public static PetDto fromEntity(Pet pet) {
        PetDto dto = new PetDto();
        dto.setPetId(pet.getPetId());
        dto.setRegistNumber(pet.getRegistNumber());
        dto.setBirthday(pet.getBirthday());
        dto.setMetday(pet.getMetday());
        dto.setName(pet.getName());
        dto.setAge(pet.getAge());
        dto.setBreed(pet.getBreed());
        dto.setSize(pet.getSize());
        dto.setWeight(pet.getWeight());
        dto.setSex(pet.getSex());
        dto.setIsNeutered(pet.getIsNeutered());
        dto.setUser(Optional.ofNullable(pet.getUser()).map(User::getUserId).orElse(null));
        PetImg image = pet.getPetImg();
        dto.setImage(
            image != null ? PetImgDto.fromEntity(image) : null
        );

        return dto;
    }
}
