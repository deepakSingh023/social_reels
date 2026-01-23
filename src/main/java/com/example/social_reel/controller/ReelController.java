package com.example.social_reel.controller;

import com.example.social_reel.entity.Reel;
import com.example.social_reel.service.ReelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
public class ReelController {

    private final ReelService reelService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Reel> createReel(
            @RequestPart("video") MultipartFile video,
            @RequestPart("tags") List<String> tags,
            Authentication authentication
    ) throws Exception {

        String userId = authentication.getName();
        return ResponseEntity.ok(
                reelService.createReel(userId, video, tags)
        );
    }

    @DeleteMapping("/{reelId}")
    public ResponseEntity<Void> deleteReel(
            @PathVariable String reelId,
            Authentication authentication
    ) {
        reelService.deleteReel(reelId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<Reel>> myReels(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reelService.getMyReels(authentication.getName())
        );
    }
}
