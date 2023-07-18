package subway.dao;

import subway.domain.Line;

public class SectionRow {

    private final Long id;
    private final Line line;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRow(Long id, Line line, Long upStationId, Long downStationId, int distance) {
        this.id = id;
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
