package subway.dto.response;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineFindResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<StationFindResponse> stations;

    public LineFindResponse(
            final Long id,
            final String name,
            final String color,
            final List<StationFindResponse> stations
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineFindResponse of(final Line line, final List<Station> stations) {
        return new LineFindResponse(
                line.getId(), line.getName(), line.getColor(),
                stations.stream()
                        .map(StationFindResponse::of)
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

    public List<StationFindResponse> getStations() {
        return this.stations;
    }
}
