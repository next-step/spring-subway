package subway.dto;

import javax.validation.constraints.NotNull;

public class LineRequest {

    @NotNull
    private String name;
    @NotNull
    private Long upStationId;
    @NotNull
    private Long downStationId;
    @NotNull
    private Integer distance;
    @NotNull
    private String color;

    private LineRequest() {
    }

    public LineRequest(String name, Long upStationId, Long downStationId, Integer distance,
        String color) {
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
        this.color = color;
    }

    public String getName() {
        return name;
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

    public String getColor() {
        return color;
    }
}
