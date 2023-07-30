package subway.ui.dto;

import subway.exception.IllegalRequestException;

public class SectionRequest {

    private long upStationId;
    private long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(final String upStationId, final String downStationId, final int distance) {
        validateStation(upStationId, downStationId);
        validateDistance(distance);

        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = distance;
    }

    private void validateStation(final String upStationId, final String downStationId) {
        if (upStationId.isBlank()) {
            throw new IllegalRequestException("상행역을 입력해야 합니다.");
        }

        if (downStationId.isBlank()) {
            throw new IllegalRequestException("하행역을 입력해야 합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new IllegalRequestException("구간 거리는 0보다 커야합니다.");
        }
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
}
