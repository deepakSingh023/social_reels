package com.example.social_reel.controller;


import com.example.social_reel.dto.DenormalizeDto;
import com.example.social_reel.service.DenormalizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reel")
public class DenormalizController {

    private final DenormalizeService denormalizeService;

    @PutMapping("/denormalize")
    public ResponseEntity<Void> reelDenormalize(
            @RequestBody DenormalizeDto data
            ){

        denormalizeService.denormalize(data.userId(), data.avatar());

        return ResponseEntity.accepted().build();

    }
}
