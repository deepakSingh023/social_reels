package com.example.social_reel.service;

import com.example.social_reel.dto.ReelCreation;
import com.example.social_reel.entity.Reel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReelService {

    Reel createReel(
            String userId,
            MultipartFile video,
            ReelCreation data
    ) throws Exception;

    void deleteReel(String reelId, String userId);

    List<Reel> getMyReels(String userId);
}
