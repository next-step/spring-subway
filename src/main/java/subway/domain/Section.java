package subway.domain;

import subway.exception.IllegalSectionException;

public class Section {

    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    public Section(final Long id, final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        validate(distance);

        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(final Long lineId, final Long upStationId, final Long downStationId, final Integer distance) {
        this(null, lineId, upStationId, downStationId, distance);
    }

    private void validate(final int distance) {
        if (distance <= 0) {
            throw new IllegalSectionException("구간 길이는 0보다 커야한다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
