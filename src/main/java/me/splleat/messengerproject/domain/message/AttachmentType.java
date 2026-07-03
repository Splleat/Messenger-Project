package me.splleat.messengerproject.domain.message;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.common.exception.BusinessException;
import me.splleat.messengerproject.common.exception.ErrorCode;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AttachmentType {
    IMAGE("image"),
    FILE("file");

    @JsonValue
    private final String value;

    public static AttachmentType from(String value) {
        return Arrays.stream(AttachmentType.values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTACHMENT_UNSUPPORTED_TYPE));
    }
}
