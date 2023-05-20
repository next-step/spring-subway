package subway.domain;

import java.util.Objects;

public class Section {
    private Long id;
    private Long downStationId;
    private Long upStationId;
    private int distance;

    public Section() {}

    public Section(Long downStationId, Long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Section(Long id, Long downStationId, Long upStationId, int distance) {
        this(downStationId, upStationId, distance);
        this.id = id;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(downStationId, section.downStationId) && Objects.equals(upStationId, section.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, downStationId, upStationId, distance);
    }
}
