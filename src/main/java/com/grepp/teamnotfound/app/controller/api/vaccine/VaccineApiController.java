package com.grepp.teamnotfound.app.controller.api.vaccine;


import com.grepp.teamnotfound.app.model.vaccination.VaccineService;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccineDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/vaccines", produces = MediaType.APPLICATION_JSON_VALUE)
public class VaccineApiController {

    private final VaccineService vaccineService;

    @GetMapping("/v1")
    public ResponseEntity<List<VaccineDto>> getAllVaccines() {
        List<VaccineDto> vaccines = vaccineService.findAll();

        return ResponseEntity.ok(vaccines);
    }

    @GetMapping("/v1/{vaccineId}")
    public ResponseEntity<VaccineDto> getVaccine(
            @PathVariable(name = "vaccineId") Long vaccineId
    ) {
        return ResponseEntity.ok(vaccineService.get(vaccineId));
    }

}
