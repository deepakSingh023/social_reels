package com.example.social_reel.dto;

import java.util.List;

public record ReelCreation(

        String username,
        String avatar,
        List<String> tags
) {
}
