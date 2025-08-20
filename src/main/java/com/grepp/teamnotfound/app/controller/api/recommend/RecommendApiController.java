package com.grepp.teamnotfound.app.controller.api.recommend;

import com.grepp.teamnotfound.app.model.recommend.DailyRecommendService;
import com.grepp.teamnotfound.app.model.recommend.GeminiService;
import com.grepp.teamnotfound.app.model.recommend.dto.GeminiResponse;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.recommend.RecommendService;
import com.grepp.teamnotfound.app.model.recommend.dto.RecommendCheckDto;
import com.grepp.teamnotfound.app.model.recommend.entity.Recommend;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendApiController {

    private final PetService petService;
    private final DailyRecommendService dailyRecommendService;
    private final RecommendService recommendService;
    private final GeminiService geminiService;

    @Operation(summary = "반려견 맞춤형 정보 제공")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v1/pet/{petId}")
    public ResponseEntity<String> getRecommend(
            @PathVariable Long petId
    ) {
        Pet pet = petService.getPet(petId);

        // 1. 반려견의 기존 DailyRecommend 있는지 체크
        Optional<Recommend> existingRecommendByDaily = dailyRecommendService.getRecommendByPet(pet);
        if(existingRecommendByDaily.isPresent()) {
            String content = existingRecommendByDaily.get().getContent();
            return ResponseEntity.ok(content);
        }

        // 2. 반려견의 종+나이, 최근 10일 생활기록 평균 데이터 생성
        RecommendCheckDto checkDto = recommendService.getRecommendCheck(pet);

        // 데이터가 없을 경우
        if(checkDto.getListDto().getWeightList().isEmpty()
                && checkDto.getListDto().getSleepTimeList().isEmpty()
                && checkDto.getListDto().getWalkingList().isEmpty()
        ) {
            return ResponseEntity.ok("최근 생활기록이 없어서 맞춤형 제안이 어려워요!\n"
                    + "생활기록을 등록하여 맞춤형 제안을 받아보세요.");
        }

        // 3. 데이터 기반 Recommend 있는지 체크 (종, 나이, 생활기록 상태)
        Optional<Recommend> existingRecommend = recommendService.getRecommendByPetStates(checkDto);

        Recommend recommend;
        if (existingRecommend.isPresent()) {
            // 추천이 이미 존재할 경우, 기존 데이터 사용
            recommend = existingRecommend.get();
        } else {
            // 추천이 존재하지 않을 경우, 맞춤형 제안 문구 생성
            GeminiResponse response = geminiService.getGemini(checkDto);
            // 순차적으로 Recommend 생성
            recommend = recommendService.createRecommendIfAbsent(checkDto, response);
        }

        // DailyRecommend에 저장
        dailyRecommendService.createDailyRecommend(pet, recommend);

        return ResponseEntity.ok(recommend.getContent());
    }

}
