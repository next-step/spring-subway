package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;

public class LineStationsResponse extends LineResponse{

    private final List<Station> stations;

    public LineStationsResponse(final Long id, final String name, final String color, final List<Station> stations) {
        super(id, name, color);
        this.stations = stations;
    }

    public static LineStationsResponse from(final Line line, final List<Station> stations) {
        return new LineStationsResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public List<Station> getStations() {
        return stations;
    }
}
