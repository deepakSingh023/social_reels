package com.example.social_reel.controller;


import com.example.social_reel.service.IncAndDecService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reel")
public class IncAndDecController {

    private final IncAndDecService incAndDecService;


    @PutMapping("/like/inc/{reelId}")
    public ResponseEntity<Void> likeInc(
            @PathVariable String reelId
    ){

        incAndDecService.incrementLike(reelId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/like/dec/{reelId}")
    public ResponseEntity<Void> likeDec(
            @PathVariable String reelId
    ){

        incAndDecService.decrementLike(reelId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/comment/inc/{reelId}")
    public ResponseEntity<Void> commentInc(
            @PathVariable String reelId
    ){

        incAndDecService.incrementComment(reelId);

        return ResponseEntity.accepted().build();


    }


    @PutMapping("/comment/dec/{reelId}")
    public ResponseEntity<Void> commentDec(
            @PathVariable String reelId
    ){

        incAndDecService.decrementComment(reelId);

        return ResponseEntity.accepted().build();


    }
}