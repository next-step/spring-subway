package subway.domain;

import java.util.Objects;

public class Section {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 다른 역이어야 합니다.";

    private Long id;
    private Long upStationId;
    private Long downStationId;
    private Long lineId;
    private Integer distance;

    public Section() {
    }

    public Section(Long upStationId, Long downStationId, Long lineId, Integer distance) {
        this(null, upStationId, downStationId, lineId, distance);
    }

    public Section(Long id, Long upStationId, Long downStationId, Long lineId, Integer distance) {
        validateDifferent(upStationId, downStationId);
        this.id = id;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.lineId = lineId;
        this.distance = distance;
    }

    private static void validateDifferent(Long upStationId, Long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStationId, section.upStationId) && Objects.equals(downStationId, section.downStationId) && Objects.equals(lineId, section.lineId) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStationId, downStationId, lineId, distance);
    }
}
