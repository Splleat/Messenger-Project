package me.splleat.messengerproject.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinIOProperties(
        String endpoint,
        String bucket,
        String accessKey,
        String secretKey
) {
    public String getBucketUrl() {
        return endpoint + "/" + bucket;
    }
}
