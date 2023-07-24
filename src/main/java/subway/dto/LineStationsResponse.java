package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;

public class LineStationsResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public LineStationsResponse(final Long id, final String name, final String color, final List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineStationsResponse from(final Line line, final List<Station> stations) {
        return new LineStationsResponse(line.getId(), line.getName(), line.getColor(), stations);
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

    public List<Station> getStations() {
        return stations;
    }
}
