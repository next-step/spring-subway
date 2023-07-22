package subway.dto;

import java.util.Objects;

public class SectionCreateRequest {

    private final long upStationId;
    private final long downStationId;
    private final int distance;

    public SectionCreateRequest(long upStationId, long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectionCreateRequest)) {
            return false;
        }
        SectionCreateRequest that = (SectionCreateRequest) o;
        return upStationId == that.upStationId && downStationId == that.downStationId && distance == that.distance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionCreateRequest{" + "upStationId=" + upStationId + ", downStationId=" + downStationId
                + ", distance=" + distance + '}';
    }
}
