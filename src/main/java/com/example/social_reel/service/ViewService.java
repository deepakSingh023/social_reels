package com.example.social_reel.service;


import com.example.social_reel.dto.ViewDto;
import com.example.social_reel.entity.Reel;
import com.example.social_reel.repository.ReelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ViewService {

    private final ViewIncrement viewIncrement;
    private final ReelRepository reelRepository;

    public Set<String> viewUpdate(ViewDto data){


        Reel reel = viewIncrement.incrementView(data.reelId());

        if (reel == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reel not found");
        }

        double popularity = calculatePopularity(reel);

        reel.setPopularityScore(popularity);

        reelRepository.save(reel);

        return reel.getSemanticTags();
    }

    private double calculatePopularity(Reel reel) {
        long hours =
                Duration.between(reel.getCreatedAt(), Instant.now()).toHours() + 1;

        return (reel.getViewCount()) / Math.pow(hours, 1.5);
    }
}