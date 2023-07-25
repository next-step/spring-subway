package subway.dto;

import java.util.List;
import subway.domain.Line;

public class LineWithStationsResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineWithStationsResponse(final Long id, final String name, final String color,
        List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineWithStationsResponse of(final Line line) {
        return new LineWithStationsResponse(
            line.getId(),
            line.getName(),
            line.getColor(),
            StationResponse.of(line.getSections().getSortedStations())
        );
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
