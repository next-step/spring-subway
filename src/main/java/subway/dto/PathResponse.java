package subway.dto;

import java.util.List;

public class PathResponse {
    private List<StationResponse> path;
    private int totalDistance;
    private int price;

    public PathResponse(List<StationResponse> path, int totalDistance, int price) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.price = price;
    }

    public List<StationResponse> getPath() {
        return path;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public int getPrice() {
        return price;
    }
}
