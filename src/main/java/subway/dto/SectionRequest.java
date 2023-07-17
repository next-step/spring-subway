package subway.dto;

public class SectionRequest {
    private final String downStationId;
    private final String upStationId;
    private final int distance;

    public SectionRequest(String downStationId, String upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public String getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
