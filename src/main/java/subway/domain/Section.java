package subway.domain;

public class Section {

    private Long id;
    private Long lineId;
    private Long downStationId;
    private Long upStationId;
    private Integer distance;

    public Section(final Long id, final Long lineId, final Long downStationId, final Long upStationId, final Integer distance) {
        validate(distance);

        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public Section(final Long lineId, final Long downStationId, final Long upStationId, final Integer distance) {
        this(null, lineId, downStationId, upStationId, distance);
    }

    private void validate(final int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 0보다 커야한다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
