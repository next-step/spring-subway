package subway.dto;

public class LineRequest {
    private String name;
    private String color;
    private String upStationId;
    private String downStationId;
    private Integer distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, String upStationId, String downStationId, Integer distance) {
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

    public String getUpStationId() {
        return upStationId;
    }

    public String getDownStationId() {
        return downStationId;
    }

    public Integer getDistance() {
        return distance;
    }
}
