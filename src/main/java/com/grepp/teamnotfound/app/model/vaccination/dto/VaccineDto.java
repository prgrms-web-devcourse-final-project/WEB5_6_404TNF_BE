package com.grepp.teamnotfound.app.model.vaccination.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VaccineDto {

    private Long vaccineId;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    private Integer period;

    @NotNull
    private Integer boosterCycle;

    @NotNull
    private Integer boosterCount;

    @NotNull
    private Integer additionalCycle;

}
