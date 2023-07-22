package subway.dto.request;

import java.util.Objects;

public final class SectionRegisterRequest {

    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRegisterRequest() {
    }

    public SectionRegisterRequest(
        Long upStationId,
        Long downStationId,
        int distance
    ) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionRegisterRequest that = (SectionRegisterRequest) o;
        return distance == that.distance && Objects.equals(downStationId, that.downStationId)
            && Objects.equals(upStationId, that.upStationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downStationId, upStationId, distance);
    }
}
