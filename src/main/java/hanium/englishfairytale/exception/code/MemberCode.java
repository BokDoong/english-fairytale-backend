package hanium.englishfairytale.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberCode {
    NOT_FOUND("M-001"),
    MEMBER_EXISTED("M-002"),
    NICKNAME_DUPLICATED("M-003"),
    NON_EXISTED_IMAGE("M-004"),
    FAILED_LOGIN("M-005"),
    INVALID_PASSWORD("M-006"),
    ;

    private final String code;
}
