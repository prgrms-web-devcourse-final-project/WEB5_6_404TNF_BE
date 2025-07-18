package com.grepp.teamnotfound.app.model.life_record.dto;

import com.grepp.teamnotfound.app.model.pet.entity.PetImg;
import com.grepp.teamnotfound.infra.code.ImgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LifeRecordListPetDto {

    private String name;
    private String url;
    private ImgType type;

    public static LifeRecordListPetDto petInfoDto(PetImg petImg){
        if (petImg == null) return null;

        return new LifeRecordListPetDto(
            petImg.getPet().getName(),
            petImg.getSavePath() + petImg.getRenamedName(),
            petImg.getType()
        );
    }

}
