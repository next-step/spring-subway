package subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineRequest {
    @NotBlank(message = "노선 이름은 공백 또는 비어있을 수 없습니다")
    private String name;
    @NotBlank(message = "노선 색상은 필수 항목입니다")
    private String color;
    @NotNull(message = "상행종점역 id는 필수 항목입니다")
    private Long upStationId;
    @NotNull(message = "하행종점역 id는 필수 항목입니다")
    private Long downStationId;
    @NotNull(message = "거리는 필수 항목입니다")
    private Integer distance;

    public LineRequest() {
    }


    public LineRequest(
            final String name,
            final String color,
            final Long upStationId,
            final Long downStationId,
            final Integer distance
    ) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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
