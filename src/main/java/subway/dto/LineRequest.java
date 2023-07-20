package subway.dto;

import org.springframework.util.Assert;

public class LineRequest {

    private String name;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;
    private String color;

    public LineRequest() {
    }

    public LineRequest(final String name,
                       final Long upStationId,
                       final Long downStationId,
                       final Integer distance,
                       final String color) {
        Assert.notNull(name, "이름을 입력해야 합니다.");
        Assert.notNull(upStationId, "상행역을 입력해야 합니다.");
        Assert.notNull(downStationId, "하행역을 입력해야 합니다.");
        Assert.notNull(distance, "거리를 입력해야 합니다.");
        Assert.notNull(color, "색깔을 입력해야 합니다.");

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
