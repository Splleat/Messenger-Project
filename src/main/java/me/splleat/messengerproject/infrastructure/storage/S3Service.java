package me.splleat.messengerproject.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignRequest;
import me.splleat.messengerproject.interfaces.rest.attachment.dto.PresignResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final MinIOProperties minIOProperties;

    private static final String KEY = "attachments/";

    public PresignResponse createPresignedUpload(PresignRequest request) {
        String objectKey = KEY + UUID.randomUUID();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(minIOProperties.bucket())
                .key(objectKey)
                .contentType(request.contentType())
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(putRequest)
                        .build()
        );

        return new PresignResponse(presigned.url().toString(), objectKey);
    }
}
