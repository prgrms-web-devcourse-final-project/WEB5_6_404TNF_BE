package com.grepp.teamnotfound.app.controller.api.mypage;


import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetWriteRequest;
import com.grepp.teamnotfound.app.controller.api.mypage.payload.VaccineWriteRequest;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccinationDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/mypage")
public class MypageApiController {

    private final PetService petService;
    private final VaccinationService vaccinationService;

    /**
     * 펫 관련 API
     **/

    @PostMapping("/v2/pets")
    public ResponseEntity<?> createPet(
        @RequestBody @Valid PetWriteRequest request
    ) {
        petService.create(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/pets/{petId}")
    public ResponseEntity<PetDto> getPet(
        @PathVariable(name = "petId") Long petId
    ) {
        PetDto petDto = petService.findOne(petId);
        return ResponseEntity.ok(petDto);
    }

    @PutMapping("/v2/pets/{petId}")
    public ResponseEntity<?> updatePet(
        @PathVariable(name = "petId") Long petId,
        @RequestBody @Valid PetWriteRequest request
    ) {
        petService.update(petId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/v2/pets/{petId}")
    public ResponseEntity<?> deletePet(
        @PathVariable(name = "petId") Long petId
    ) {
        petService.delete(petId);
        return ResponseEntity.ok().build();
    }



    /**
     * 펫의 백신 관련 API
     **/

    @PostMapping("/v1/pets/{petId}/vaccination")
    public ResponseEntity<?> createVaccination(
        @PathVariable(name = "petId") Long petId,
        @RequestBody @Valid List<VaccineWriteRequest> requests
    ) {
        vaccinationService.savePetVaccinations(petId, requests);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/v1/pets/{petId}/vaccination")
    public ResponseEntity<List<VaccinationDto>> getVaccination(
        @PathVariable(name = "petId") Long petId
    ) {
        List<VaccinationDto> response = vaccinationService.findPetVaccination(petId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/v1/pets/{petId}/vaccination")
    public ResponseEntity<?> updateVaccination(
        @PathVariable(name = "petId") Long petId,
        @RequestBody @Valid List<VaccineWriteRequest> requests
    ) {
        vaccinationService.savePetVaccinations(petId, requests);
        return ResponseEntity.ok().build();
    }
}
