package subway.ui.dto;

import subway.exception.IllegalRequestException;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        validateName(name);

        this.name = name;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalRequestException("이름을 입력해야 합니다.");
        }

        if (name.length() > 255) {
            throw new IllegalRequestException("이름 길이는 255자를 초과할 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }
}
