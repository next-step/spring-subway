package subway.dto.response;

import subway.domain.ShortPath;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class PathFindResponse {
    private List<StationResponse> stations;
    private long distance;

    public PathFindResponse() {
    }

    private PathFindResponse(final List<StationResponse> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathFindResponse of(final ShortPath shortPath) {
        return new PathFindResponse(shortPath.getStations().stream()
                .map(StationResponse::of)
                .collect(toList()), shortPath.getDistance());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
