package com.example.social_reel.service;

import com.example.social_reel.entity.Reel;
import com.example.social_reel.repository.ReelRepository;
import com.example.social_reel.util.VideoCompressor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService {

    private final S3Client s3Client;
    private final ReelRepository reelRepository;
    private final SemanticTagResolver tagResolver;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-base-url}")
    private String publicBaseUrl; // e.g. https://bucket.accountid.r2.dev

    @Override
    public Reel createReel(
            String userId,
            MultipartFile video,
            List<String> rawTags
    ) throws Exception {

        validateVideo(video);

        File compressed = VideoCompressor.compressToFile(video);

        String videoKey = "reels/" + UUID.randomUUID() + ".mp4";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(videoKey)
                        .contentType("video/mp4")
                        .build(),
                RequestBody.fromFile(compressed)
        );

        compressed.delete();

        String publicVideoUrl = publicBaseUrl + "/" + videoKey;

        Reel reel = new Reel();
        reel.setUserId(userId);
        reel.setVideoUrl(publicVideoUrl); // ✅ PUBLIC URL
        reel.setRawTags(rawTags);
        reel.setSemanticTags(tagResolver.resolve(rawTags));
        reel.setCreatedAt(Instant.now());

        return reelRepository.save(reel);
    }

    @Override
    public void deleteReel(String reelId, String userId) {

        Reel reel = reelRepository.findById(reelId)
                .orElseThrow(() -> new RuntimeException("Reel not found"));

        if (!reel.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        String key = reel.getVideoUrl()
                .replace(publicBaseUrl + "/", "");

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );

        reelRepository.delete(reel);
    }

    @Override
    public List<Reel> getMyReels(String userId) {
        return reelRepository.findByUserId(userId);
    }

    private void validateVideo(MultipartFile video) {

        if (video.isEmpty())
            throw new IllegalArgumentException("Video is empty");

        if (video.getContentType() == null ||
                !video.getContentType().startsWith("video/"))
            throw new IllegalArgumentException("Invalid video type");

        if (video.getSize() > 60 * 1024 * 1024)
            throw new IllegalArgumentException("Video too large");
    }
}
