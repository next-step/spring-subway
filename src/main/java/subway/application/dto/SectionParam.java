package subway.application.dto;

public class SectionParam {

    private final long lineId;
    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionParam(long lineId, long upStationId, long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getLineId() {
        return lineId;
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
