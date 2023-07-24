package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;

public class LineStationsResponse {

    private final LineResponse lineResponse;
    private final List<Station> stations;

    public LineStationsResponse(final Long id, final String name, final String color, final List<Station> stations) {
        this.lineResponse = new LineResponse(id, name, color);
        this.stations = stations;
    }

    public static LineStationsResponse from(final Line line, final List<Station> stations) {
        return new LineStationsResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public LineResponse getLineResponse() {
        return lineResponse;
    }

    public List<Station> getStations() {
        return stations;
    }
}
