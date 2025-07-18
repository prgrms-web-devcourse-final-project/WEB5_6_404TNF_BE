package com.grepp.teamnotfound.app.controller.api.life_record;

import com.grepp.teamnotfound.app.controller.api.article.payload.PageInfo;
import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordListRequest;
import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordListResponse;
import com.grepp.teamnotfound.app.controller.api.life_record.payload.LifeRecordData;
import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.life_record.LifeRecordService;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordDto;
import com.grepp.teamnotfound.app.model.life_record.dto.LifeRecordListDto;
import com.grepp.teamnotfound.app.model.pet.PetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "생활기록 리스트 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v1/users/life-record-list")
    public ResponseEntity<LifeRecordListResponse> getLifeRecordList(
            @AuthenticationPrincipal Principal principal,
            @ModelAttribute @Valid LifeRecordListRequest request
    ){
        PageRequest pageable = PageRequest.of(request.getPage() - 1, 12); // size 12 고정

        Page<LifeRecordListDto> page = lifeRecordService.searchLifeRecords(principal.getUserId(), request, pageable);

        PageInfo pageInfo = PageInfo.builder()
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return ResponseEntity.ok(new LifeRecordListResponse(page.getContent(), pageInfo));
    }

    @Operation(summary = "보호자의 반려견 목록 조회") // 특정 반려견 생활기록만 보기 위하여 필요
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v1/users/pet-list")
    public ResponseEntity<Map<String, List<Map<Long, String>>>> getPetList(
            @AuthenticationPrincipal Principal principal
    ){
        List<Map<Long, String>> petLists = petService.findPetListByUserId(principal.getUserId());

        return ResponseEntity.ok(Map.of("data", petLists));
    }

    @Operation(summary = "생활기록 상세정보 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v2/detail/{lifeRecordId}")
    public ResponseEntity<Map<String, LifeRecordData>> getLifeRecordDetail(
            @PathVariable Long lifeRecordId
    ){
        LifeRecordData lifeRecord = lifeRecordService.getLifeRecord(lifeRecordId);

        return ResponseEntity.ok(Map.of("data", lifeRecord));
    }

    @Operation(summary = "기존 생활기록 데이터 존재 여부 체크")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v2/pets/{petId}/check")
    public ResponseEntity<?> checkLifeRecord(
            @PathVariable Long petId,
            @RequestParam LocalDate date
    ){
        // 기존 데이터가 있으면 기존 데이터 반환
        Optional<Long> lifeRecordIdOptional = lifeRecordService.findLifeRecordId(petId, date);

        if (lifeRecordIdOptional.isPresent()) {
            Long lifeRecordId = lifeRecordIdOptional.get();
            LifeRecordData lifeRecord = lifeRecordService.getLifeRecord(lifeRecordId);

            return ResponseEntity.ok(Map.of("data", lifeRecord));
        }

        return ResponseEntity.ok("데이터 등록 가능");
    }

    @Operation(summary = "생활기록 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/v2/create")
    public ResponseEntity<Map<String, Long>> registLifeRecord(
            @RequestBody LifeRecordData data
    ){
        LifeRecordDto dto = LifeRecordDto.toDto(data);
        Long lifeRecordId = lifeRecordService.createLifeRecord(dto);

        return ResponseEntity.ok(Map.of("lifeRecordId", lifeRecordId));
    }

    @Operation(summary = "생활기록 수정")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/v2/{lifeRecordId}/update")
    public ResponseEntity<Map<String, Long>> modifyLifeRecord(
            @PathVariable Long lifeRecordId,
            @RequestBody LifeRecordData data
    ){
        LifeRecordDto dto = LifeRecordDto.toDto(data);
        lifeRecordService.updateLifeRecord(lifeRecordId, dto);

        return ResponseEntity.ok(Map.of("lifeRecordId", lifeRecordId));
    }

    @Operation(summary = "생활기록 삭제")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/v2/{lifeRecordId}/delete")
    public ResponseEntity<String> deleteLifeRecord(
            @PathVariable Long lifeRecordId
    ){
        lifeRecordService.deleteLifeRecord(lifeRecordId);

        return ResponseEntity.ok("삭제 성공");
    }

}
