package hekireki.sanjijiksong.domain.following.controller;

import hekireki.sanjijiksong.domain.following.dto.FollowingResponse;
import hekireki.sanjijiksong.domain.following.service.FollowingService;
import hekireki.sanjijiksong.global.security.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Following", description = "가게 팔로우 관련 API")
@RestController
@RequestMapping("/api/v1/followings")
@RequiredArgsConstructor
public class FollowingController {
    private final FollowingService followingService;

    @Operation(summary = "가게 팔로우", description = "특정 가게를 팔로우합니다.")
    @ApiResponse(responseCode = "200", description = "팔로우 성공")
    @PostMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<FollowingResponse> followStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FollowingResponse response = followingService.followStore(userDetails.getUser().getId(), storeId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 언팔로우", description = "팔로우 중인 가게를 언팔로우합니다.")
    @ApiResponse(responseCode = "200", description = "언팔로우 성공")
    @DeleteMapping("/stores/{storeId}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> unfollowStore(
            @PathVariable Long storeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        followingService.unfollowStore(userDetails.getUser().getId(), storeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팔로우 중인 가게 목록 조회", description = "사용자가 팔로우 중인 가게 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<FollowingResponse>> getFollowingStores(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<FollowingResponse> response = followingService.getFollowingStores(userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "가게 팔로워 수 조회", description = "특정 가게의 팔로워 수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/stores/{storeId}/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable Long storeId) {
        Long count = followingService.getFollowingCount(storeId);
        return ResponseEntity.ok(count);
    }
} 