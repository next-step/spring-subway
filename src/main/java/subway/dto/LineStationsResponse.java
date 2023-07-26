package subway.dto;

import java.util.List;
import subway.domain.Line;

public class LineStationsResponse extends LineResponse{

    private final List<StationResponse> stations;

    public LineStationsResponse(
            final Long id, final String name, final String color, final List<StationResponse> stations) {
        super(id, name, color);
        this.stations = stations;
    }

    public static LineStationsResponse from(final Line line, final List<StationResponse> stations) {
        return new LineStationsResponse(line.getId(), line.getName().getValue(), line.getColor(), stations);
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
