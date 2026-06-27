package me.splleat.messengerproject.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
        String endpoint,
        String region,
        String bucket,
        String accessKey,
        String secretKey,
        long maxSizeBytes
) {
    public String getBucketUrl() {
        if (hasCustomEndpoint()) {
            return endpoint + "/" + bucket;
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com";
    }

    public boolean hasCustomEndpoint() {
        return endpoint != null && !endpoint.isBlank();
    }

    public boolean hasStaticCredentials() {
        return accessKey != null && !accessKey.isBlank();
    }
}
