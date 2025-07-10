package com.grepp.teamnotfound.app.controller.api.profile;

import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.user.UserService;
import com.grepp.teamnotfound.app.model.user.dto.UserDto;
import com.grepp.teamnotfound.app.model.user.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/profile")
public class ProfileApiController {

    private final UserService userService;
    private final PetService petService;

    @GetMapping("/v1/{userId}")
    public ResponseEntity<UserDto> getUser(
        @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @GetMapping("/v1/pet/{userId}")
    public ResponseEntity<List<PetDto>> getUserPets(
        @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(petService.findByUserId(userId));
    }

//    @GetMapping("/v1/board/{userId}")
//    public ResponseEntity<PetDto> getUserBoard(
//        @PathVariable(name = "userId") Long userId
//    ) {
//        return ResponseEntity.ok(petService.findOne(userId));
//    }


}

