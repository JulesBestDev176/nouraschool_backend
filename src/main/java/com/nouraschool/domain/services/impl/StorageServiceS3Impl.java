package com.nouraschool.domain.services.impl;

import com.nouraschool.domain.services.StorageService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

/**
 * Implémentation S3 du stockage (upload, URLs pré-signées 15 min, delete). Utilise le client
 * Quarkus S3 ; le presigner est construit avec la même région / endpoint que le client.
 */
@ApplicationScoped
public class StorageServiceS3Impl implements StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final String bucket;
    private final Duration presignDuration;

    public StorageServiceS3Impl(
            S3Client s3,
            @ConfigProperty(name = "app.storage.bucket") String bucket,
            @ConfigProperty(name = "app.storage.presign-expiration-minutes") long presignExpirationMinutes,
            @ConfigProperty(name = "quarkus.s3.aws.region", defaultValue = "us-east-1") String region,
            @ConfigProperty(name = "quarkus.s3.endpoint-override", defaultValue = "") Optional<String> endpointOverride) {
        this.s3 = s3;
        this.bucket = bucket;
        this.presignDuration = Duration.ofMinutes(presignExpirationMinutes);
        S3Presigner.Builder presignerBuilder = S3Presigner.builder()
                .region(Region.of(region));
        if (endpointOverride.isPresent() && !endpointOverride.get().isBlank()) {
            presignerBuilder.endpointOverride(URI.create(endpointOverride.get()));
        }
        this.presigner = presignerBuilder.build();
    }

    @Override
    public String upload(String key, InputStream input, String contentType, long contentLength) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();
        RequestBody body = contentLength >= 0
                ? RequestBody.fromInputStream(input, contentLength)
                : RequestBody.fromInputStream(input, -1);
        s3.putObject(request, body);
        return key;
    }

    @Override
    public Optional<String> getPresignedUrl(String key) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(presignDuration)
                    .getObjectRequest(getRequest)
                    .build();
            PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
            return Optional.of(presigned.url().toExternalForm());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3.deleteObject(request);
    }
}
