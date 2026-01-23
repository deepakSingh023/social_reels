package com.example.social_reel.service;

import com.example.social_reel.repository.TagMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SemanticTagResolver {

    private final TagMappingRepository repository;

    public Set<String> resolve(List<String> rawTags) {

        Set<String> semanticTags = new HashSet<>();

        for (String raw : rawTags) {
            String normalized = raw.toLowerCase().trim();

            repository.findByRawTag(normalized)
                    .ifPresent(mapping ->
                            semanticTags.add(mapping.getSemanticTag()));
        }

        return semanticTags;
    }
}


