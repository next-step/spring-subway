package subway.dto;

public class LineRequest {

    private final String name;
    private final Long upStationId;
    private final Long downStationId;
    private final Long distance;
    private final String color;

    public LineRequest(final String name, final Long upStationId, final Long downStationId, final Long distance, final String color) {
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public Long getUpStationId() {
        return this.upStationId;
    }

    public Long getDownStationId() {
        return this.downStationId;
    }

    public Long getDistance() {
        return this.distance;
    }

    public String getColor() {
        return this.color;
    }
}
