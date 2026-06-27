package me.splleat.messengerproject.common.util;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.storage.StorageProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageUrlMapper {
    private final StorageProperties properties;

    public String resolve(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        return properties.getBucketUrl() + "/" + objectKey;
    }
}
