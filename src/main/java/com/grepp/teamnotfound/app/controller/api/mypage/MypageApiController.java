package com.grepp.teamnotfound.app.controller.api.mypage;


import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetCreateRequest;
import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetEditRequest;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/v1/pets")
    public ResponseEntity<Long> createPet(
        @RequestBody @Valid PetCreateRequest request
    ) {
        Long createdPetId = petService.create(request);
        return new ResponseEntity<>(createdPetId, HttpStatus.CREATED);
    }

    @GetMapping("/v1/pets/{petId}")
    public ResponseEntity<PetDto> getPet(
        @PathVariable(name = "petId") Long petId
    ) {
        return ResponseEntity.ok(petService.findOne(petId));
    }

    @PutMapping("/v1/pets/{petId}")
    public ResponseEntity<Long> updatePet(
        @PathVariable(name = "petId") Long petId,
        @RequestBody @Valid PetEditRequest request
    ) {
        petService.update(petId, request);
        return ResponseEntity.ok(petId);
    }

    @DeleteMapping("/v1/pets/{petId}")
    public ResponseEntity<Long> deletePet(
        @PathVariable(name = "petId") Long petId
    ) {
        petService.delete(petId);
        return ResponseEntity.ok(petId);
    }

}
