package subway.ui.dto;

import subway.exception.IllegalRequestException;

public class LineRequest {

    private String name;
    private long upStationId;
    private long downStationId;
    private int distance;
    private String color;

    public LineRequest() {
    }

    public LineRequest(final String name,
                       final long upStationId,
                       final long downStationId,
                       final int distance,
                       final String color) {
        validateName(name);
        validateColor(color);
        validateDistance(distance);

        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.color = color;
    }

    private void validateName(final String name) {
        if (name.isBlank()) {
            throw new IllegalRequestException("이름을 입력해야 합니다.");
        }

        if (name.length() > 255) {
            throw new IllegalRequestException("이름 길이는 255자를 초과할 수 없습니다.");
        }
    }

    private void validateColor(final String color) {
        if (color.isBlank()) {
            throw new IllegalRequestException("색상을 입력해야 합니다.");
        }

        if (color.length() > 20) {
            throw new IllegalRequestException("색상 길이는 20자를 초과할 수 없습니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalRequestException("구간 거리는 0보다 커야합니다.");
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
