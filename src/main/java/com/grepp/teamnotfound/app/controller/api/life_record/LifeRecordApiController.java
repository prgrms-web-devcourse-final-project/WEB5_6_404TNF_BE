package com.grepp.teamnotfound.app.controller.api.life_record;

import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordListResponse;
import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordData;
import com.grepp.teamnotfound.app.model.life_record.LifeRecordService;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordDto;
import com.grepp.teamnotfound.app.model.note.NoteService;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/life-record")
public class LifeRecordApiController {

    private final PetService petService;
    private final LifeRecordService lifeRecordService;
    private final NoteService noteService;

    // 보호자의 반려견 생활기록 리스트 조회
    @GetMapping("/v1/users/{userId}")
    public ResponseEntity<Map<String, List<LifeRecordListResponse>>> getLifeRecordList(
            @PathVariable Long userId,
            @RequestParam(required = false) Long petId,
            @RequestParam Integer page,
            @RequestParam Integer size
    ){
        // TODO: 생활기록 리스트 조회 Service 구현

        // Mock Data
        LifeRecordListResponse record1 = LifeRecordListResponse.builder()
                .createdAt(OffsetDateTime.now())
                .noteId(1L).name("이마음").savePath("이미지 저장경로").weight(36.3F)
                .content("더워서 기분 안 좋아 보임").build();
        LifeRecordListResponse record2 = LifeRecordListResponse.builder()
                .createdAt(OffsetDateTime.now())
                .noteId(1L).name("이마음").savePath("이미지 저장경로").weight(36.3F)
                .content("더워서 기분 안 좋아 보임").build();
        LifeRecordListResponse record3 = LifeRecordListResponse.builder()
                .createdAt(OffsetDateTime.now())
                .noteId(1L).name("이마음").savePath("이미지 저장경로").weight(36.3F)
                .content("더워서 기분 안 좋아 보임").build();

        List<LifeRecordListResponse> list = new ArrayList<>(List.of(record1, record2, record3));

        return ResponseEntity.ok(Map.of("data", list));
    }

    // 보호자의 반려견 목록 조회 (특정 반려견 생활기록만 보기 위하여 필요)
    @GetMapping("/v1/users/{userId}/pet-list")
    public ResponseEntity<Map<String, List<Map<Long, String>>>> getPetList(
            @PathVariable Long userId
    ){
        // TODO: 반려견 목록 조회 Service 구현

        // Mock Data
        Map<Long, String> pet1 = new HashMap<>();
        pet1.put(1L, "이마음");
        Map<Long, String> pet2 = new HashMap<>();
        pet2.put(2L, "김행복");
        Map<Long, String> pet3 = new HashMap<>();
        pet3.put(3L, "박슬픔");

        List<Map<Long, String>> list = new ArrayList<>(List.of(pet1, pet2, pet3));

        return ResponseEntity.ok(Map.of("data",list));
    }

    // 생활기록 상세정보 조회
    @GetMapping("/v1/pets/{petId}")
    public ResponseEntity<Map<String, LifeRecordData>> getLifeRecordDetail(
        @PathVariable Long petId,
        @RequestParam LocalDate date
    ){
        Pet pet = petService.getPet(petId);
        LifeRecordData lifeRecord = lifeRecordService.getLifeRecord(petId, date);

        return ResponseEntity.ok(Map.of("data", lifeRecord));
    }


    // 날짜, 애완동물 기준으로 기존 생활기록 데이터 있는지 체크
    @GetMapping("/v1/pets/{petId}/check")
    public ResponseEntity<Map<String, LifeRecordData>> checkLifeRecord(
            @PathVariable Long petId,
            @RequestParam LocalDate date
    ){
        // 기존 데이터가 있으면 기존 데이터 반환
        if(noteService.existsLifeRecord(petId, date)){
            LifeRecordData lifeRecord = lifeRecordService.getLifeRecord(petId, date);

            return ResponseEntity.ok(Map.of("data", lifeRecord));
        }

        return ResponseEntity.ok().build();
    }

    // 생활기록 등록
    @PostMapping
    public ResponseEntity<String> registLifeRecord(
            @RequestBody LifeRecordData data
    ){
        LifeRecordDto dto = new LifeRecordDto();
        dto = dto.toDto(data);
        lifeRecordService.createLifeRecord(dto);

        return ResponseEntity.ok("등록 성공");
    }

    // 생활기록 수정
    @PutMapping
    public ResponseEntity<Map<String, LifeRecordData>> modifyLifeRecord(
            @RequestBody LifeRecordData data
    ){
        LifeRecordDto dto = new LifeRecordDto();
        dto = dto.toDto(data);
        lifeRecordService.updateLifeRecord(data.getPetId(), dto);

        return ResponseEntity.ok(Map.of("data", data));
    }
    // 생활기록 삭제
    @DeleteMapping("/v1/pets/{petId}/delete")
    public ResponseEntity<String> deleteLifeRecord(
            @PathVariable Long petId,
            @RequestParam LocalDate date
    ){
        lifeRecordService.deleteLifeRecord(petId, date);

        return ResponseEntity.ok("삭제 성공");
    }

}
