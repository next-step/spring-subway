package subway.dto;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotNull(message = "상행역 아이디는 null 일 수 없습니다.")
    @Positive(message = "상행역 아이디는 양수여야합니다.")
    private Long upStationId;

    @NotNull(message = "하행역 아이디는 null 일 수 없습니다.")
    @Positive(message = "하행역 아이디는 양수여야합니다.")
    private Long downStationId;

    @NotNull(message = "거리는 null 일 수 없습니다.")
    @Positive(message = "거리는 양수여야합니다.")
    private Long distance;

    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }


    public SectionRequest() {
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
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
        SectionRequest that = (SectionRequest) o;
        return Objects.equals(upStationId, that.upStationId) && Objects.equals(
                downStationId, that.downStationId) && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "SectionRequest{" +
                "upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
