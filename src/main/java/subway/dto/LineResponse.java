package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse() {
        /* no-op */
    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(final Line line, final List<Station> stations) {
        return new LineResponse(
                line.getId(), line.getName(), line.getColor(),
                stations.stream()
                        .map(StationResponse::of)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public List<StationResponse> getStations() {
        return this.stations;
    }
}
