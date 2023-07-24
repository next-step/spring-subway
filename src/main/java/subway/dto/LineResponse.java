package subway.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import subway.domain.Line;
import subway.domain.Station;

public class LineResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(
             final Long id,
             final String name,
             final String color,
             final List<StationResponse> stations
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                Collections.emptyList());
    }

    public static LineResponse of(final Line line, final List<Long> sortedStationIds, final Map<Long, Station> stations) {
        final List<StationResponse> stationResponses = sortedStationIds
                .stream()
                .map(id -> StationResponse.of(stations.get(id)))
                .collect(Collectors.toList());

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationResponses);
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
        return this.stations;
    }
}
