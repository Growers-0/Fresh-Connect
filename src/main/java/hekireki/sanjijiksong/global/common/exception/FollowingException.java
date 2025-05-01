package hekireki.sanjijiksong.global.common.exception;

import lombok.Getter;

@Getter
public class FollowingException extends RuntimeException {
    private final ErrorCode errorCode;

    public FollowingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
} 