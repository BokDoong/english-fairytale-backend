package hanium.englishfairytale.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunityCode {

    POST_NOT_FOUND("CM-001"),
    EXISTED_POST("CM-002"),
    EXISTED_REPORT("CM-003"),
    ;

    private final String code;
}
