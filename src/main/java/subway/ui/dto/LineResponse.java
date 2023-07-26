package subway.ui.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Line;
import subway.domain.Station;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineResponse {

    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static LineResponse of(Line line, List<Station> stations) {
        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
