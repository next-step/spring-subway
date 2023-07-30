package subway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import subway.exception.SubwayException;

public final class SectionRequest {

    private final long upStationId;
    private final long downStationId;
    private final int distance;

    @JsonCreator
    public SectionRequest(final String upStationId, final String downStationId, final int distance) {
        validateUpStationId(upStationId);
        validateDownStationId(downStationId);
        validateDistance(distance);

        this.upStationId = convertStationId(upStationId);
        this.downStationId = convertStationId(downStationId);
        this.distance = distance;
    }

    private static long convertStationId(final String stationId) {
        try {
            return Long.parseLong(stationId);
        } catch (NumberFormatException exception) {
            throw new SubwayException("올바른 역 id를 입력해야 합니다.");
        }
    }

    private void validateUpStationId(final String upStationId) {
        if (upStationId == null || upStationId.isBlank()) {
            throw new SubwayException("상행역을 입력해야 합니다.");
        }
    }

    private void validateDownStationId(final String downStationId) {
        if (downStationId == null || downStationId.isBlank()) {
            throw new SubwayException("하행역을 입력해야 합니다.");
        }
    }

    private void validateDistance(final int distance) {
        if (distance <= 0) {
            throw new SubwayException("거리는 0 이하의 수가 될 수 없습니다.");
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
