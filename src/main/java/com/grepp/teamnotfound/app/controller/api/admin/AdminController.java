package com.grepp.teamnotfound.app.controller.api.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @Operation(summary = "관리자 권한 테스트용")
    @PostMapping("v1/hello")
    public ResponseEntity<String> adminTest(){
        return ResponseEntity.ok("Admin 사용자만 접근 가능");
    }

}
