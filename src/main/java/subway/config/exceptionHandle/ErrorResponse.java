package subway.config.exceptionHandle;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime time;
    private final UUID logId;

    public static ErrorResponse of(HttpStatus code, UUID logId, Exception ex) {
        return ErrorResponse.builder()
                .code(Integer.toString(code.value()))
                .message(ex.getMessage())
                .time(LocalDateTime.now())
                .logId(logId)
                .build();
    }
}
