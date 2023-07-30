package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public final class LineWithStationsResponse {

    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineWithStationsResponse() {
    }

    private LineWithStationsResponse(final LineResponse lineResponse,
                                     final List<StationResponse> stations) {
        this.id = lineResponse.getId();
        this.name = lineResponse.getName();
        this.color = lineResponse.getColor();
        this.stations = stations;
    }

    public static LineWithStationsResponse of(final Line line, List<Station> stations) {
        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
        return new LineWithStationsResponse(LineResponse.of(line), stationResponses);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
