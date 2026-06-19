package me.splleat.messengerproject.common.util;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.storage.MinIOProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageUrlMapper {
    private final MinIOProperties properties;

    public String resolve(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return null;
        }

        return properties.getBucketUrl() + "/" + objectKey;
    }
}
