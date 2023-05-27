package subway.config.exceptionHandle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
    private final String message;
    private final LocalDateTime time;
    private final UUID logId;

    public static ErrorResponse of(UUID logId, Exception ex) {
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .time(LocalDateTime.now())
                .logId(logId)
                .build();
    }
}
