package subway.dto.request;

import javax.validation.constraints.NotNull;

public class SectionAdditionRequest {

    @NotNull
    private final long upStationId;
    @NotNull
    private final long downStationId;
    @NotNull
    private final int distance;

    public SectionAdditionRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
