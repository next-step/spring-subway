package subway.dto;

import org.springframework.util.Assert;

public class LineRequest {

    private String name;
    private long upStationId;
    private long downStationId;
    private int distance;
    private String color;

    public LineRequest() {
    }

    public LineRequest(final String name,
                       final long upStationId,
                       final long downStationId,
                       final int distance,
                       final String color) {
        Assert.notNull(name, "이름을 입력해야 합니다.");
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

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    public String getColor() {
        return color;
    }
}
