package subway.domain;

public class Section  {

    private Long downStationId;

    private Long upStationId;

    private Double distance;

    public Section(final Long downStationId, final Long upStationId, final Double distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }
}
