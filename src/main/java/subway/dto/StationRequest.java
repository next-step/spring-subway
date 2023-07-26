package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.exception.IllegalRequestException;

public final class StationRequest {

    private static final int MAX_NAME_LENGTH = 255;

    private String name;

    private StationRequest() {
    }

    @JsonCreator
    public StationRequest(final String name) {
        validateName(name);

        this.name = name;
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalRequestException("역의 이름은 최소 한 글자 이상이어야 합니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalRequestException("역의 이름은 " + MAX_NAME_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }
}
