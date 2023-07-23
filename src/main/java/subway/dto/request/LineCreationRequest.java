package subway.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LineCreationRequest {

    @NotBlank
    private String name;
    @NotNull
    private long upStationId;
    @NotNull
    private long downStationId;
    @NotNull
    private int distance;
    @NotBlank
    private String color;

    public LineCreationRequest() {
    }

    public LineCreationRequest(String name, long upStationId, long downStationId, Integer distance,
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
