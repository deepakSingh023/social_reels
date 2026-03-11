package com.example.social_reel.service;


import com.example.social_reel.dto.*;
import com.example.social_reel.entity.Reel;
import com.example.social_reel.repository.ReelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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



    public FeedResponse fetchFeed(FetchReelDto req){

        UserInterest interest = req.data();
        int limit = req.req().limit();

        Instant cursorTime = req.req().cursor() != null
                ? Instant.parse(req.req().cursor())
                : Instant.now();

        Set<String> topTags = interest != null
                ? getTopTags(interest, 5)
                : Set.of();

        List<Reel> feed = new ArrayList<>();

        if (!topTags.isEmpty()) {
            feed.addAll(
                    reelRepository.findBySemanticTagsInAndCreatedAtLessThanOrderByCreatedAtDesc(
                            topTags,
                            cursorTime,
                            PageRequest.of(0, limit / 2)
                    )
            );
        }

        feed.addAll(
                reelRepository.findByCreatedAtLessThanOrderByPopularityScoreDesc(
                        cursorTime,
                        PageRequest.of(0, limit / 4)
                )
        );

        feed.addAll(
                reelRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(
                        cursorTime,
                        PageRequest.of(0, limit / 4)
                )
        );

        Collections.shuffle(feed);

        List<Reel> uniqueFeed =
                feed.stream()
                        .distinct()
                        .limit(limit)
                        .toList();

        List<ReelResponse> response =
                uniqueFeed.stream()
                        .map(r -> new ReelResponse(
                                r.getId(),
                                r.getUsername(),
                                r.getAvatar(),
                                r.getVideoUrl(),
                                r.getRawTags(),
                                r.getViewCount(),
                                r.getCreatedAt(),
                                r.getUserId()
                        ))
                        .toList();

        // create next cursor
        String nextCursor = null;

        if(!uniqueFeed.isEmpty()){
            nextCursor =
                    uniqueFeed
                            .get(uniqueFeed.size()-1)
                            .getCreatedAt()
                            .toString();
        }

        return new FeedResponse(response,nextCursor);

    }

    private Set<String> getTopTags(UserInterest interest, int n) {
        return interest.interests()
                .entrySet()
                .stream()
                .sorted((a, b) ->
                        Double.compare(
                                b.getValue().score(),
                                a.getValue().score()
                        )
                )
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private double calculatePopularity(Reel reel) {
        long hours =
                Duration.between(reel.getCreatedAt(), Instant.now()).toHours() + 1;

        return (reel.getViewCount()) / Math.pow(hours, 1.5);
    }
}