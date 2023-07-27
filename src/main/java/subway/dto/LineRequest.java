package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.exception.IllegalRequestException;

public final class LineRequest {

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_COLOR_LENGTH = 20;

    private final String name;
    private final long upStationId;
    private final long downStationId;
    private final int distance;
    private final String color;

    @JsonCreator
    public LineRequest(final String name,
                       final long upStationId,
                       final long downStationId,
                       final int distance,
                       final String color) {
        validateName(name);
        validateUpStationId(upStationId);
        validateDownStationId(downStationId);
        validateDistance(distance);
        validateColor(color);

        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.color = color;
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalRequestException("노선의 이름은 최소 한 글자 이상이어야 합니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalRequestException("노선의 이름은 " + MAX_NAME_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    private void validateUpStationId(final long upStationId) {
        if (upStationId < 0) {
            throw new IllegalRequestException("상행역의 id는 음수가 될 수 없습니다.");
        }
    }

    private void validateDownStationId(final long downStationId) {
        if (downStationId < 0) {
            throw new IllegalRequestException("상행역의 id는 음수가 될 수 없습니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalRequestException("거리는 0 이하의 수가 될 수 없습니다.");
        }
    }

    private void validateColor(final String color) {
        if (color == null || color.isBlank()) {
            throw new IllegalRequestException("노선의 색깔은 최소 한 글자 이상이어야 합니다.");
        }
        if (color.length() > MAX_COLOR_LENGTH) {
            throw new IllegalRequestException("색깔 문자열의 길이는 " + MAX_COLOR_LENGTH + "자를 넘을 수 없습니다.");
        }
    }

    public String getName() {
        return name;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public String getColor() {
        return color;
    }
}
