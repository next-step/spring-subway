package subway.api.dto;

import javax.validation.constraints.NotBlank;

public class RouteRequest {
    @NotBlank
    private String sourceStationName;
    @NotBlank
    private String destinationStationName;

    public RouteRequest() {
    }

    public RouteRequest(String sourceStationName, String destinationStationName) {
        this.sourceStationName = sourceStationName;
        this.destinationStationName = destinationStationName;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public String getDestinationStationName() {
        return destinationStationName;
    }
}
