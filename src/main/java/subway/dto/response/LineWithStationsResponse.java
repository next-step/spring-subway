package subway.dto.response;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineWithStationsResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineWithStationsResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public static LineWithStationsResponse of(Line line, List<Station> stations) {
        return new LineWithStationsResponse(line.getId(), line.getName(), line.getColor(),
                stations);
    }

    public Long getId() {
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
