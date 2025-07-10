package com.grepp.teamnotfound.app.model.pet.dto;

import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.infra.code.ImgType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetImgDto {

    private Long petImgId;
    private String savePath;
    private ImgType type;
    private String originName;
    private String renamedName;

    public static PetImgDto fromEntity(PetImg petImg) {
        if (petImg == null) {
            return null;
        }

        return new PetImgDto(
            petImg.getPetImgId(),
            petImg.getSavePath(),
            petImg.getType(),
            petImg.getOriginName(),
            petImg.getRenamedName()
        );
    }
}
