package com.grepp.teamnotfound.app.model.pet.dto;

import com.grepp.teamnotfound.app.model.pet.code.PetSize;
import com.grepp.teamnotfound.app.model.pet.code.PetType;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.user.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PetDto {

    private Long petId;
    private String registNumber;
    private LocalDate birthday;
    private LocalDate metday;
    private String name;
    private PetType breed;
    private PetSize size;
    private Double weight;
    private Boolean sex;
    private Boolean isNeutered;
    private Long user;
    private String imgUrl;

    public static PetDto fromEntity(Pet pet) {
        PetDto dto = new PetDto();
        dto.setPetId(pet.getPetId());
        dto.setRegistNumber(pet.getRegistNumber());
        dto.setBirthday(pet.getBirthday());
        dto.setMetday(pet.getMetday());
        dto.setName(pet.getName());
        dto.setBreed(pet.getBreed());
        dto.setSize(pet.getSize());
        dto.setWeight(pet.getWeight());
        dto.setSex(pet.getSex());
        dto.setIsNeutered(pet.getIsNeutered());
        dto.setUser(Optional.ofNullable(pet.getUser()).map(User::getUserId).orElse(null));

        return dto;
    }
}
